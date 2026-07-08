package com.shopflow.payment.presentation.api.controllers;

import com.shopflow.payment.application.commands.CreatePaymentLinkCommand;
import com.shopflow.payment.application.commands.CreatePaymentLinkCommandHandler;
import com.shopflow.payment.application.commands.ProcessMoMoIpnCommand;
import com.shopflow.payment.application.commands.ProcessMoMoIpnCommandHandler;
import com.shopflow.payment.presentation.api.requests.MoMoIpnRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final CreatePaymentLinkCommandHandler createPaymentLinkCommandHandler;
    private final ProcessMoMoIpnCommandHandler processMoMoIpnCommandHandler;

    public PaymentController(CreatePaymentLinkCommandHandler createPaymentLinkCommandHandler,
                             ProcessMoMoIpnCommandHandler processMoMoIpnCommandHandler) {
        this.createPaymentLinkCommandHandler = createPaymentLinkCommandHandler;
        this.processMoMoIpnCommandHandler = processMoMoIpnCommandHandler;
    }

    @PostMapping("/link")
    public ResponseEntity<Map<String, String>> createPaymentLink(@Valid @RequestBody CreatePaymentLinkCommand command) {
        String payUrl = createPaymentLinkCommandHandler.handle(command);
        return ResponseEntity.ok(Map.of("payUrl", payUrl));
    }

    @PostMapping("/momo-ipn")
    public ResponseEntity<Void> processMoMoIpn(@RequestBody MoMoIpnRequest request) {
        ProcessMoMoIpnCommand command = ProcessMoMoIpnCommand.builder()
                .partnerCode(request.getPartnerCode())
                .orderId(request.getOrderId())
                .requestId(request.getRequestId())
                .amount(request.getAmount())
                .orderInfo(request.getOrderInfo())
                .orderType(request.getOrderType())
                .transId(request.getTransId())
                .resultCode(request.getResultCode())
                .message(request.getMessage())
                .payType(request.getPayType())
                .responseTime(request.getResponseTime())
                .extraData(request.getExtraData())
                .signature(request.getSignature())
                .build();
        
        processMoMoIpnCommandHandler.handle(command);
        // MoMo expects 204 No Content for successful IPN acknowledgement
        return ResponseEntity.noContent().build();
    }

}
