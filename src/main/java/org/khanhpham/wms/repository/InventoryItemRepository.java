package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}