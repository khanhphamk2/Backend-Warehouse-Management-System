package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
}