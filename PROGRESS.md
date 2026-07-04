# ShopFlow - Báo Cáo Tiến Độ & Onboarding (Cập nhật liên tục)

Tài liệu này đóng vai trò như một bản đồ dẫn đường (Roadmap & Status Report) dành cho bất kỳ thành viên mới nào tham gia vào dự án ShopFlow. Đọc kỹ tài liệu này giúp bạn nắm bắt nhanh chóng kiến trúc, những gì đã hoàn thành và những gì sắp diễn ra.

---

## 1. Kiến Trúc Tổng Thể (The Big Picture)
ShopFlow là một hệ thống E-Commerce được xây dựng theo chuẩn **Enterprise Microservices**. 
- **Core Pattern:** Domain-Driven Design (DDD), Hexagonal Architecture (Ports & Adapters).
- **Giao tiếp:** Bất đồng bộ qua **Apache Kafka** (Event-Driven) và đồng bộ qua REST API.
- **Quản lý dữ liệu phân tán:** Saga Pattern (Choreography) kết hợp với Transactional Outbox Pattern.
- **Tech Stack:** Spring Boot 3.2.3, Java 21, PostgreSQL, Redis, Kafka, Resilience4j, JWT.

---

## 2. Tiến Độ Các Module (Status by Module)

### 🟢 2.1. `shopflow-shared` (Core Library)
- **Vai trò:** Chứa các class cốt lõi dùng chung cho mọi module.
- **Đã hoàn thành:**
  - `BaseEntity`: Xương sống của mọi Aggregate Root, xử lý `createdAt`, `updatedAt` và quản lý danh sách `DomainEvent`.
  - Cấu hình chung cho Redis (`RedisConfig`) với `StringRedisSerializer` và `GenericJackson2JsonRedisSerializer`.
  - Hệ thống Exception chung (`DomainException`, `ErrorCode`).

### 🟢 2.2. `shopflow-identity` (Authentication & Security)
- **Vai trò:** Trái tim bảo mật, cấp phát Token và quản lý người dùng.
- **Đã hoàn thành:**
  - Kiến trúc JWT an toàn với Access Token (ngắn hạn) và Refresh Token (dài hạn lưu DB).
  - Tích hợp **Resilience4j Rate Limiter** để chống brute-force đăng nhập.
  - **(VỪA HOÀN THÀNH)** Luồng Đăng ký qua Email bằng mã OTP:
    1. Chuyển trạng thái User sang `PENDING_VERIFICATION`.
    2. Sinh mã OTP 6 số, lưu vào **Redis** với TTL 5 phút.
    3. Phát sự kiện `UserRegisterEvent` ra Outbox/Kafka.
  - API `/api/v1/auth/verify-otp` để thu hồi mã OTP, đối chiếu Redis và mở khóa (`ACTIVE`) tài khoản.

### 🟢 2.3. `shopflow-order` & `shopflow-inventory` (Business Core)
- **Vai trò:** Xử lý nghiệp vụ Đặt hàng và Tồn kho.
- **Đã hoàn thành:**
  - Cài đặt thành công **Saga Pattern (Choreography)**: 
    - Order tạo ra -> Bắn event `OrderCreatedEvent`.
    - Inventory nghe event -> Giữ chỗ kho (Reserve Stock).
    - Nếu kho hết hàng -> Inventory bắn `StockReservationFailedEvent`.
    - Order nghe event lỗi -> Tự động chuyển trạng thái Order sang `CANCELLED` (Compensating Transaction).
  - Cài đặt thành công **Transactional Outbox Pattern**: Đảm bảo không bao giờ mất event gửi xuống Kafka nhờ vào Spring Scheduler quét table `outbox_events` định kỳ.

### 🟢 2.4. `shopflow-notification` (Mailer Service)
- **Vai trò:** Chuyên trị việc gửi tin nhắn/Email. Là "người đưa thư" thầm lặng.
- **Đã hoàn thành:**
  - Setup thành công Kafka Consumer lắng nghe topic `identity-events`.
  - Tích hợp `JavaMailSender` và render Email HTML xịn sò.
  - Đã fix xong lỗi Dependency Hell (ung đột SLF4J 1.x và 2.x do Kafka mang lại) bằng Spring Boot BOM.

---

## 3. Chuyện Gì Đang Diễn Ra Hôm Nay? (Current Focus)
Chúng ta vừa dứt điểm xong luồng **Đăng ký tài khoản bằng OTP Email**.
- Đã giải quyết xong lỗi Spring Boot không quét thấy bean `RedisTemplate` ở file ngoài bằng `@SpringBootApplication(scanBasePackages = "com.shopflow")`.
- Đã giải quyết xong lỗi lệch kiểu Generic của Java (`<String, Object>` vs `<Object, Object>`).
- Đã gỡ bỏ tính năng auto-configure của OAuth2 Resource Server để nhường sân khấu cho hệ thống JJWT tự code.
👉 **Việc đang làm:** Kỹ sư đang thực hiện chạy test End-to-End bằng Postman (Gọi API Đăng ký -> Check Redis -> Check Kafka -> Mở hộp thư Gmail xem có nhận được OTP HTML không).

---

## 4. Hành Trình Tiếp Theo (What's Next?)

Sau khi luồng OTP được confirm là chạy xanh mượt, team sẽ tiến đánh các Phase tiếp theo để biến ShopFlow thành một cỗ máy Enterprise thực thụ:

### 🟡 Bước 1: Trạm Thu Phí Giao Thông (API Gateway)
- Khởi tạo module `shopflow-gateway` (Spring Cloud Gateway).
- Chuyển toàn bộ cấu hình CORS từ các module con lên Gateway quản lý tập trung.
- Chuyển Logic kiểm tra JWT (Authentication) lên Gateway. Các service đằng sau (Order, Inventory) chỉ việc tin tưởng Gateway.

### 🟡 Bước 2: Gia cố Hệ Thống (Resilience)
- Phân tích và bổ sung **Circuit Breaker** (Cầu dao tự ngắt) cho những chỗ cần gọi đồng bộ (REST/gRPC) để tránh hiệu ứng domino chết chùm.
- Bổ sung hệ thống Caching (Cache-Aside Pattern) dùng Redis cho những endpoint tra cứu sản phẩm nặng nề.

### 🟡 Bước 3: Quan Sát & Đo Lường (Observability)
- Không có Monitor thì hệ thống như người mù. Sẽ tích hợp **Prometheus** & **Grafana** (thông qua Spring Boot Actuator).
- Cài đặt **Zipkin / Micrometer Tracing** để truy vết đường đi của một Request đi xuyên qua Gateway -> Order -> Inventory.

### 🟡 Bước 4: Triển Khai (DevOps & CI/CD)
- Viết `docker-compose.yml` để nhấc toàn bộ (Postgres, Redis, Kafka, Zookeeper, và các Service) lên chạy bằng 1 lệnh duy nhất.
- Viết GitHub Actions Pipeline để tự động chạy Unit Test và Build Docker Image khi có Code Push.
