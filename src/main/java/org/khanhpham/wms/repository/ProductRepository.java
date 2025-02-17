package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}