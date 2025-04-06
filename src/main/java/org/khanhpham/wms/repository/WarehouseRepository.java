package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByName(String name);
    boolean existsByWarehouseCode(String warehouseCode);
}