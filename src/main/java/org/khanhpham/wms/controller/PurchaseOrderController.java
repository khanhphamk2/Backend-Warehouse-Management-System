package org.khanhpham.wms.controller;

import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.PurchaseOrderDTO;
import org.khanhpham.wms.domain.model.PurchaseOrder;
import org.khanhpham.wms.domain.request.PurchaseOrderRequest;
import org.khanhpham.wms.service.PurchaseOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("${spring.data.rest.base-path}/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderRequest request) {
        PurchaseOrderDTO createdOrder = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(@PathVariable Long id, @RequestBody PurchaseOrder purchaseOrder) {
        PurchaseOrder updatedOrder = purchaseOrderService.updatePurchaseOrder(id, purchaseOrder);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> findById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> findAll() {
        return ResponseEntity.ok(purchaseOrderService.findAll());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>> findBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(purchaseOrderService.findBySupplier(supplierId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> findByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.findByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PurchaseOrder>> findByDateRange(
            @RequestParam Instant startDate, @RequestParam Instant endDate) {
        return ResponseEntity.ok(purchaseOrderService.findByDateRange(startDate, endDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseOrder> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updateOrderStatus(id, status));
    }

    @GetMapping("/{id}/recalculate-total")
    public ResponseEntity<BigDecimal> recalculateTotalAmount(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.recalculateTotalAmount(id));
    }
}
