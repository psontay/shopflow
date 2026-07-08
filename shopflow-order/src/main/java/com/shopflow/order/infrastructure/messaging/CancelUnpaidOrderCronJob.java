package com.shopflow.order.infrastructure.messaging;

import com.shopflow.order.application.commands.CancelOrderCommand;
import com.shopflow.order.application.commands.CancelOrderCommandHandler;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.models.OrderStatus;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class CancelUnpaidOrderCronJob {

    private static final Logger log = LoggerFactory.getLogger(CancelUnpaidOrderCronJob.class);

    private final OrderRepository orderRepository;
    private final CancelOrderCommandHandler cancelOrderCommandHandler;

    public CancelUnpaidOrderCronJob(OrderRepository orderRepository,
                                    CancelOrderCommandHandler cancelOrderCommandHandler) {
        this.orderRepository = orderRepository;
        this.cancelOrderCommandHandler = cancelOrderCommandHandler;
    }

    @Scheduled(fixedRate = 60000)
    public void scanAndCancelUnpaidOrders() {
        log.info("Cronjob start to fetch...");

        try {
            Instant timeoutThreshold = Instant.now()
                                              .minus(15, ChronoUnit.MINUTES);

            List<Order> expiredOrders = orderRepository
                    .findByOrderStatusAndCreatedAtBefore(OrderStatus.PENDING_PAYMENT, timeoutThreshold);

            if (expiredOrders.isEmpty()) {
                log.info("There is no order expired");
                return;
            }

            log.warn("⚠️ [CRONJOB] Found order {} expired. Processing to cancel...", expiredOrders.size());

            for (Order order : expiredOrders) {
                try {
                    CancelOrderCommand command = new CancelOrderCommand(
                            order.getId(),
                            "Expired payment time"
                    );
                    cancelOrderCommandHandler.handle(command);
                    log.info("Cancel expired order: {}", order.getId());
                } catch (Exception e) {
                    log.error("Error when cancel expired order: {}", order.getId(), e);
                }
            }

        } catch (Exception e) {
            log.error("System error when fetching order expire", e);
        }
    }

}
