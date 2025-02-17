package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}