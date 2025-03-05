package org.khanhpham.wms.service;

import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.PurchaseOrderDTO;
import org.khanhpham.wms.domain.model.PurchaseOrder;
import org.khanhpham.wms.domain.request.PurchaseOrderRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDTO createPurchaseOrder(PurchaseOrderRequest request);
    PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrder);
    PurchaseOrder findById(Long id);
    List<PurchaseOrder> findAll();
    List<PurchaseOrder> findBySupplier(Long supplierId);
    List<PurchaseOrder> findByStatus(OrderStatus status);
    List<PurchaseOrder> findByDateRange(Instant startDate, Instant endDate);
    void deletePurchaseOrder(Long id);
    PurchaseOrder updateOrderStatus(Long id, OrderStatus status);
    BigDecimal recalculateTotalAmount(Long purchaseOrderId);
}
