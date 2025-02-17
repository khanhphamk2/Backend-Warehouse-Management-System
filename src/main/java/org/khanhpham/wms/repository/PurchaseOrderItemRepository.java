package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
}