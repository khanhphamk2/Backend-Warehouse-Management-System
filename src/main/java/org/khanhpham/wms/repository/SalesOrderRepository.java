package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
}