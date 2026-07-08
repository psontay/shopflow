package com.shopflow.order.presentation.api.controllers;

import com.shopflow.order.application.commands.CreateOrderCommand;
import com.shopflow.order.application.commands.CreateOrderCommandHandler;
import com.shopflow.order.application.queries.OrderQueryHandler;
import com.shopflow.order.application.queries.OrderSummaryResponse;
import com.shopflow.order.presentation.api.dto.CreateOrderRequest;
import com.shopflow.order.presentation.api.dto.CreateOrderResponse;
import com.shopflow.shared.presentation.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderCommandHandler createOrderHandler;
    private final OrderQueryHandler orderQueryHandler;

    public OrderController(CreateOrderCommandHandler createOrderHandler, OrderQueryHandler orderQueryHandler) {
        this.createOrderHandler = createOrderHandler;
        this.orderQueryHandler = orderQueryHandler;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = request.toCommand();
        UUID orderId = createOrderHandler.handle(command);
        CreateOrderResponse response = new CreateOrderResponse(orderId, "Order create success");
        ApiResponse<CreateOrderResponse> wrappedResponse = ApiResponse.created(response,
                                                                               "Create order success with order" +
                                                                                       " id: " + orderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(wrappedResponse);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getCustomerOrders(@PathVariable UUID customerId) {
        List<OrderSummaryResponse> data = orderQueryHandler.getCustomerOrders(customerId);
        ApiResponse<List<OrderSummaryResponse>> wrappedResponse = ApiResponse.success(data, "Get order history " +
                "success for user id: " + customerId.toString());
        return ResponseEntity.ok(wrappedResponse);
    }

}
