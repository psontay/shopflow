import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class TestHmac {
    public static void main(String[] args) {
        String accessKey = "klm05TvNCyandNDO";
        String amount = "50000";
        String extraData = "";
        String ipnUrl = "https://abcd.ngrok-free.app/api/v1/payments/momo-ipn";
        String orderId = "550e8400-e29b-41d4-a716-446655440000";
        String orderInfo = "Thanh toan giay the thao Shopflow";
        String partnerCode = "MOMOBKUN20180529";
        String redirectUrl = "http://localhost:3000/payment-result";
        String requestId = "84ca10b3-12eb-4d87-88c8-74e89206075d";
        String requestType = "captureWallet";
        String secretKey = "at67qH6mk8w5Y1nAwMo2NdOJTgXONzWA";

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

        System.out.println("Raw Data:");
        System.out.println(rawData);
        System.out.println("Generated Signature:");
        System.out.println(calculateHMac(rawData, secretKey));
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
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
