package org.khanhpham.wms.repository;

import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.model.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Page<PurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);
    Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable);
    Page<PurchaseOrder> findByOrderDateBetween(Instant begin, Instant end, Pageable pageable);
}