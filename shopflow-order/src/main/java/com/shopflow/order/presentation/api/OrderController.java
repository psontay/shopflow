package com.shopflow.order.presentation.api;

import com.shopflow.order.application.commands.CreateOrderCommand;
import com.shopflow.order.application.commands.CreateOrderCommandHandler;
import com.shopflow.order.presentation.api.dto.CreateOrderRequest;
import com.shopflow.order.presentation.api.dto.CreateOrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderCommandHandler createOrderHandler;

    public OrderController(CreateOrderCommandHandler createOrderHandler) {
        this.createOrderHandler = createOrderHandler;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = request.toCommand();
        UUID orderId = createOrderHandler.handle(command);
        CreateOrderResponse response = new CreateOrderResponse(orderId, "Order create success");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
