package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.domain.dto.CategoryDTO;
import org.khanhpham.wms.domain.entity.Category;
import org.khanhpham.wms.domain.mapper.CategoryMapper;
import org.khanhpham.wms.domain.request.CategoryRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.CategoryRepository;
import org.khanhpham.wms.service.CacheService;
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY = "Category";
    private static final String REDIS_PREFIX_ID = "category:id:";
    private static final String REDIS_PREFIX_NAME = "category:name:";
    private static final String REDIS_PREFIX_PATTERN = "categories:page:";
    private static final Duration REDIS_TTL = Duration.ofHours(2);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CacheService cacheService;

    @Override
    @Transactional
    public CategoryDTO createCategory(@NotNull CategoryRequest categoryRequest) {
        validateCategoryExistence(categoryRequest.getName());
        Category category = categoryMapper.convertToEntity(categoryRequest);
        CategoryDTO savedCategory = save(category);

        updateCategoryCaches(savedCategory);

        return savedCategory;
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, @NotNull CategoryRequest categoryRequest) {
        Category category = findById(id);
        String oldName = category.getName();

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        CategoryDTO updatedCategory = save(category);

        if (!oldName.equals(updatedCategory.getName())) {
            cacheService.evictByKeys(REDIS_PREFIX_NAME + oldName);
        }

        updateCategoryCaches(updatedCategory);

        return updatedCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);

        cacheService.evictByKeys(
                REDIS_PREFIX_ID + category.getId(),
                REDIS_PREFIX_NAME + category.getName()
        );
        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        String key = REDIS_PREFIX_ID + id;
        return getOrCache(key, new TypeReference<>() {},
                () -> categoryMapper.convertToDTO(findById(id))
        );
    }

    @Override
    public CategoryDTO getCategoryByName(String name) {
        String key = REDIS_PREFIX_NAME + name;
        Long categoryId = getOrCache(key, new TypeReference<>() {},
                () -> findByName(name)).getId();
        return getCategoryById(categoryId);
    }

    @Override
    public PaginationResponse<CategoryDTO> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir) {
        String key = REDIS_PREFIX_PATTERN + pageNumber + ":" + pageSize + ":" + sortBy + ":" + sortDir;
        return getOrCache(key, new TypeReference<>() {},
                () -> getAllCategoriesFromDB(pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Override
    public List<Category> getAllById(Set<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    // ----------- Private Helpers -----------

    private CategoryDTO save(Category category) {
        return categoryMapper.convertToDTO(categoryRepository.save(category));
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "id", id));
    }

    private CategoryDTO findByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "name", name));
    }

    private @NotNull PaginationResponse<CategoryDTO> getAllCategoriesFromDB(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Category> categories = categoryRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<CategoryDTO> content = categories.getContent()
                .stream()
                .map(categoryMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, categories);
    }

    private void validateCategoryExistence(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ResourceAlreadyExistException(CATEGORY, "name", name);
        }
    }

    private void updateCategoryCaches(CategoryDTO dto) {
        cacheService.cacheValue(REDIS_PREFIX_ID + dto.getId(), dto, REDIS_TTL);
        cacheService.cacheValue(REDIS_PREFIX_NAME + dto.getName(), dto.getId(), REDIS_TTL);

        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
    }

    private <T> T getOrCache(String key, TypeReference<T> typeRef, Supplier<T> dbSupplier) {
        return cacheService.getCached(key, typeRef, dbSupplier, REDIS_TTL);
    }
}
