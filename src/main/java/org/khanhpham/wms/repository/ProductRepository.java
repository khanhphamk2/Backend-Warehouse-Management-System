package org.khanhpham.wms.repository;

import jakarta.validation.constraints.NotNull;
import org.khanhpham.wms.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    Optional<Product> findBySku(String sku);
    Page<Product> findBySupplierId(Long supplierId, Pageable pageable);
    Page<Product> findByPrice(@NotNull BigDecimal price, Pageable pageable);
    Page<Product> findByPriceBetween(@NotNull BigDecimal min, @NotNull BigDecimal max, Pageable pageable);
    Page<Product> findByCategoriesId(Long categoryId, Pageable pageable);
    Boolean existsBySku(String sku);
}