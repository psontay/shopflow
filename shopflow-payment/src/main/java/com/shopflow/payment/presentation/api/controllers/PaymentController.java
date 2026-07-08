package com.shopflow.payment.presentation.api.controllers;

import com.shopflow.payment.application.commands.CreatePaymentLinkCommand;
import com.shopflow.payment.application.commands.CreatePaymentLinkCommandHandler;
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

    public PaymentController(CreatePaymentLinkCommandHandler createPaymentLinkCommandHandler) {
        this.createPaymentLinkCommandHandler = createPaymentLinkCommandHandler;
    }

    @PostMapping("/link")
    public ResponseEntity<Map<String, String>> createPaymentLink(@Valid @RequestBody CreatePaymentLinkCommand command) {
        String payUrl = createPaymentLinkCommandHandler.handle(command);
        return ResponseEntity.ok(Map.of("payUrl", payUrl));
    }

}
