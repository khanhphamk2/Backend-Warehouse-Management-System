package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}