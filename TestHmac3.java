import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class TestHmac3 {
    public static void main(String[] args) {
        String accessKey = "F8BBA842ECF85";
        String amount = "50000";
        String extraData = "";
        String ipnUrl = "https://abcd.ngrok-free.app/api/v1/payments/momo-ipn";
        String orderId = "550e8400-e29b-41d4-a716-446655440000";
        String orderInfo = "Thanh toan giay the thao Shopflow";
        String partnerCode = "MOMO";
        String redirectUrl = "http://localhost:3000/payment-result";
        String requestId = "84ca10b3-12eb-4d87-88c8-74e89206075d";
        String requestType = "captureWallet";
        String secretKey = "K951B6PE1waPeJiGkNjz1BGtwg52C7kZ";

        String rawData = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = calculateHMac(rawData, secretKey);
        System.out.println(signature);
    }

    public static String calculateHMac(String data, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
