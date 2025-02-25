package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.CategoryDTO;
import org.khanhpham.wms.domain.model.Category;
import org.khanhpham.wms.domain.request.CategoryRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    CategoryDTO createCategory(CategoryRequest categoryRequest);
    CategoryDTO updateCategory(Long id, CategoryRequest categoryRequest);
    void deleteCategory(Long id);
    CategoryDTO getCategoryById(Long id);
    CategoryDTO getCategoryByName(String name);
    PaginationResponse<CategoryDTO> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir);
    List<Category> getAllById(Set<Long> ids);
}
