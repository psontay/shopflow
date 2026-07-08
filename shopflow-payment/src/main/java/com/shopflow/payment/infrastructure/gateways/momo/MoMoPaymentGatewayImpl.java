package com.shopflow.payment.infrastructure.gateways.momo;

import com.shopflow.payment.application.ports.PaymentGatewayPort;
import com.shopflow.payment.domain.models.Payment;
import com.shopflow.payment.domain.models.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MoMoPaymentGatewayImpl implements PaymentGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(MoMoPaymentGatewayImpl.class);

    private final MoMoConfig config;
    private final RestTemplate restTemplate;

    public MoMoPaymentGatewayImpl(MoMoConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String generatePayUrl(Payment payment, String orderInfo) {
        String requestId = payment.getId()
                                  .toString();
        String orderId = payment.getOrderId()
                                .toString();
        String amount = String.valueOf(payment.getAmount()
                                              .amount()
                                              .longValue());

        String requestType = "captureWallet";
        String extraData = "";

        String rawData = "accessKey=" + config.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + config.getIpnUrl() +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + config.getPartnerCode() +
                "&redirectUrl=" + config.getReturnUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;
        log.info("RAW DATA: {}", rawData);

        String signature = HmacUtil.calculateHMac(rawData, config.getSecretKey());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", config.getPartnerCode());
        requestBody.put("partnerName", "Shopflow System");
        requestBody.put("storeId", "ShopflowStore");
        requestBody.put("requestId", requestId);
        requestBody.put("amount", Long.parseLong(amount));
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", config.getReturnUrl());
        requestBody.put("ipnUrl", config.getIpnUrl());
        requestBody.put("lang", "vi");
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);

        log.info("Sending request to MoMo for Order: {}", orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(config.getApiEndpoint(), entity, Map.class);

        if (response != null && response.containsKey("payUrl")) {
            return (String) response.get("payUrl");
        }

        log.error("MoMo return error: {}", response);
        throw new RuntimeException("Gen payment link fail");
    }

    @Override
    public boolean verifySignature(String rawData, String signature) {
        String expectedSignature = HmacUtil.calculateHMac(rawData, config.getSecretKey());
        return expectedSignature.equals(signature);
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.MOMO;
    }

}
