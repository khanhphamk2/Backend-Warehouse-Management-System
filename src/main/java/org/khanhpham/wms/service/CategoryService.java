package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.CategoryDTO;
import org.khanhpham.wms.domain.request.CategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryRequest categoryRequest);
    CategoryDTO updateCategory(Long id, CategoryRequest categoryRequest);
    void deleteCategory(Long id);
    CategoryDTO getCategoryById(Long id);
    CategoryDTO getCategoryByName(String name);
    List<CategoryDTO> getAllCategories();
}
