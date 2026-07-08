# ShopFlow: Enterprise E-Commerce Microservices

ShopFlow is a highly scalable, event-driven e-commerce platform built with Spring Boot. It demonstrates advanced enterprise architecture patterns, focusing on robust distributed system design, resilience, and clean code principles.

## Architecture Highlights

*   **Microservices Architecture**: Loosely coupled services communicating asynchronously.
*   **Domain-Driven Design (DDD)**: Strict adherence to Bounded Contexts, Aggregate Roots, and Domain Events.
*   **Hexagonal Architecture (Ports & Adapters)**: Clear separation between application core, infrastructure, and presentation layers.
*   **Event-Driven Architecture**: Inter-service communication via Apache Kafka.

## Implemented Features

### 1. Advanced Distributed Patterns
*   **Saga Pattern (Choreography)**: Distributed transactions across `Order` and `Inventory` services. Ensures data consistency without two-phase commit (2PC) by leveraging compensating transactions (e.g., automatically canceling an order if stock reservation fails).
*   **Transactional Outbox Pattern**: Guarantees at-least-once message delivery to Kafka. Domain events are persisted atomically with business entities in PostgreSQL and relayed asynchronously by a dedicated worker job.

### 2. Identity & Security (shopflow-identity)
*   **JWT Authentication**: Secure access using Access Tokens and persistent Refresh Tokens.
*   **OTP Email Verification**: User registration triggers a One-Time Password sent via email, cached in Redis with a 5-minute Time-To-Live (TTL).
*   **Rate Limiting**: Protected endpoints (like Login) using `Resilience4j` (combining IP-based and User-based limiting) to prevent brute-force attacks.

### 3. Service Modules
*   **shopflow-gateway**: Spring Cloud Gateway for centralized routing, rate limiting, and CORS configuration.
*   **shopflow-shared**: Core domain abstractions, shared exceptions, and centralized configuration (e.g., Redis setup).
*   **shopflow-order**: Manages order lifecycles and handles compensating actions from inventory failures.
*   **shopflow-inventory**: Manages product catalog and stock reservations.
*   **shopflow-payment**: Integrates with external payment gateways (MoMo V2). Uses Redisson Distributed Locks for idempotent IPN Webhook handling to prevent race conditions.
*   **shopflow-notification**: Dedicated "dumb" consumer service that listens to Kafka events to dispatch HTML emails via Google SMTP.

## Tech Stack

*   **Backend**: Java 21, Spring Boot 3.x, Spring Data JPA, Spring Security
*   **Database**: PostgreSQL
*   **Messaging**: Apache Kafka
*   **Caching & TTL**: Redis
*   **Resilience**: Resilience4j
*   **Build Tool**: Maven

## Future Roadmap

The following features are planned for upcoming phases:

### Phase 1: Distributed System Resilience
*   **Circuit Breaker & Retry Patterns**: Implementing `Resilience4j` Circuit Breakers for any synchronous HTTP/gRPC calls between microservices to prevent cascading failures.
*   **Advanced Caching**: Implementing Redis Cache-Aside pattern for high-traffic endpoints (e.g., product catalogs) with Cache Eviction policies.

### Phase 3: Observability & Monitoring
*   **Metrics**: Spring Boot Actuator integrated with Prometheus.
*   **Dashboards**: Grafana for visualizing system health, Kafka lag, and JVM metrics.
*   **Distributed Tracing**: Micrometer Tracing (Zipkin/Jaeger) to trace requests across multiple microservices.
*   **Centralized Logging**: Structured JSON logging aggregated via ELK Stack (Elasticsearch, Logstash, Kibana) or Promtail/Loki.

### Phase 4: DevOps & Infrastructure (CI/CD)
*   **Containerization**: Multi-stage Dockerfiles for optimized, lightweight application images.
*   **Orchestration**: Kubernetes (K8s) manifests including Deployments, Services, ConfigMaps, Secrets, and Liveness/Readiness probes.
*   **Automated Pipelines**: GitHub Actions for automated testing, building, and pushing images to GitHub Container Registry (GHCR).

### Phase 5: Code Quality & Documentation
*   **Static Analysis**: Integration with SonarQube and Checkstyle.
*   **Testing**: Achieving >= 70% code coverage.
*   **Architecture Decision Records (ADR)**: Documenting structural choices and trade-offs.

## Getting Started

### Prerequisites
* Docker and Docker Compose installed.
* Java 21 and Maven (if running locally without Docker).

### Running with Docker Compose
1. Clone the repository and navigate to the project root.
2. Build and start all services (Databases, Kafka, Redis, and Spring Boot apps):
   ```bash
   docker-compose up --build -d
   ```
3. Check the logs of any service to ensure it started successfully:
   ```bash
   docker-compose logs -f shopflow-order
   ```
4. Access the Zipkin tracing dashboard at: `http://localhost:9411`
5. Access the Grafana monitoring dashboard at: `http://localhost:3000` (if profiles are enabled).

### Stopping the Services
To stop and remove all containers, networks, and volumes:
```bash
docker-compose down -v
```
