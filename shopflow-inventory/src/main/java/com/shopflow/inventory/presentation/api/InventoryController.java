package com.shopflow.inventory.presentation.api;

import com.shopflow.inventory.application.commands.ReserveStockCommand;
import com.shopflow.inventory.application.commands.ReserveStockCommandHandler;
import com.shopflow.inventory.application.queries.CheckAvailabilityQuery;
import com.shopflow.inventory.application.queries.CheckAvailabilityQueryHandler;
import com.shopflow.inventory.application.queries.ProductAvailabilityResponse;
import com.shopflow.inventory.presentation.api.dto.ReserveStockRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final ReserveStockCommandHandler reserveStockCommandHandler;
    private final CheckAvailabilityQueryHandler checkAvailabilityQueryHandler;

    public InventoryController(ReserveStockCommandHandler reserveStockCommandHandler,
                               CheckAvailabilityQueryHandler checkAvailabilityQueryHandler) {
        this.reserveStockCommandHandler = reserveStockCommandHandler;
        this.checkAvailabilityQueryHandler = checkAvailabilityQueryHandler;
    }

    @GetMapping("/{productId}/availability")
    public ResponseEntity<ProductAvailabilityResponse> checkAvailability(@PathVariable UUID productId) {
        CheckAvailabilityQuery query = new CheckAvailabilityQuery(productId);
        ProductAvailabilityResponse response = checkAvailabilityQueryHandler.handle(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@Valid @RequestBody ReserveStockRequest request) {
        ReserveStockCommand command = new ReserveStockCommand(request.orderId(),
                                                              request.productId(),
                                                              request.quantity());
        reserveStockCommandHandler.handle(command);
        return ResponseEntity.ok()
                             .build();
    }

}
