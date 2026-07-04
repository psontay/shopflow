package com.shopflow.order.infrastructure.clients;

import com.shopflow.order.application.services.StockCheckerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpInventoryClient implements StockCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(HttpInventoryClient.class);
    private final RestTemplate restTemplate;

    public HttpInventoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "inventoryService",
            fallbackMethod = "fallbackCheckStock")
    public boolean checkStock(String productId) {
        logger.info("Calling Inventory Service Port 8083 to checking");
        String url = "http://localhost:8083/api/v1/inventory/" + productId + "/availability";
        restTemplate.getForEntity(url, Object.class);
        return true;
    }

    public boolean fallbackCheckStock(String productId, Throwable throwable) {
        logger.error("Circuit Breaker Open => Block to Inventory. Cause: {}", throwable.getMessage());
        return false;
    }

}
