package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByName(String name);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}