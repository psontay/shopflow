package com.shopflow.order.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.repositories.OrderRepository;
import com.shopflow.order.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockReservedEventHandler extends AbstractIdempotentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(StockReservedEventHandler.class);
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public StockReservedEventHandler(
            JpaProcessedEventRepository jpaProcessedEventRepository,
            OrderRepository orderRepository, ObjectMapper objectMapper) {
        super(jpaProcessedEventRepository);
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "StockReservedEvent".equals(eventType);
    }

    @Override
    protected void processBusinessLogic(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);
        String orderIdStr = rootNode.path("aggregateId")
                                    .asText();
        UUID orderId = UUID.fromString(orderIdStr);
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new OrderDomainException(OrderErrorCode.ORDER_NOT_FOUND));
        order.markAsAwaitingPayment();
        orderRepository.save(order);
        log.info("Update order {} to PENDING_PAYMENT success", orderId);
    }

}
