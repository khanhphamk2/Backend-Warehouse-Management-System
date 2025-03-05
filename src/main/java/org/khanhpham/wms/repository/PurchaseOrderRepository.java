package org.khanhpham.wms.repository;

import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByStatus(OrderStatus status);
    List<PurchaseOrder> findByOrderDateBetween(Instant begin, Instant end);
}