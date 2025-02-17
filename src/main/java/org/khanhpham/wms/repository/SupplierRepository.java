package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}