package org.khanhpham.wms.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.khanhpham.wms.domain.dto.CategoryDTO;
import org.khanhpham.wms.domain.model.Category;
import org.khanhpham.wms.domain.request.CategoryRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.CategoryRepository;
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY = "Category";
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    private CategoryDTO convertToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    private Category convertToEntity(CategoryRequest categoryRequest) {
        return Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
    }

    @Override
    public CategoryDTO createCategory(CategoryRequest categoryRequest) {
        Category category = convertToEntity(categoryRequest);
        try {
            Category savedCategory = categoryRepository.save(category);
            return convertToDTO(savedCategory);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceAlreadyExistException(CATEGORY, "name", categoryRequest.getName());
        }
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "id", id));
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        return convertToDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "id", id));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "id", id));
    }

    @Override
    public CategoryDTO getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "name", name));
    }

    @Override
    public PaginationResponse<CategoryDTO> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Category> categories = categoryRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<CategoryDTO> content = categories.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, categories);
    }

    @Override
    public List<Category> getAllById(Set<Long> ids) {
        return categoryRepository.findAllById(ids)
                .stream()
                .toList();
    }
}
