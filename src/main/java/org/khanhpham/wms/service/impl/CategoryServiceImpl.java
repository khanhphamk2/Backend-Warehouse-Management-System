package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY = "Category";
    private static final String REDIS_PREFIX_ID = "categories:id:";
    private static final String REDIS_PREFIX_NAME = "categories:name:";
    private static final String REDIS_PREFIX_PAGE = "categories:page:";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    @CachePut(value = "categories", key = "#result.id()")
    public CategoryDTO createCategory(CategoryRequest categoryRequest) {
        try {
            Category category = categoryMapper.convertToEntity(categoryRequest);
            return categoryMapper.convertToDTO(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            log.error("Category already exists: {}", categoryRequest.getName());
            throw new ResourceAlreadyExistException(CATEGORY, "name", categoryRequest.getName());
        }
    }

    @Override
    @Transactional
    @CachePut(value = "categories", key = "#result.id()")
    public CategoryDTO updateCategory(Long id, @NotNull CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "id", id));

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        //        // Update cache
//        String idKey = REDIS_PREFIX_ID + id;
//        String nameKey = REDIS_PREFIX_NAME + updatedCategory.getName();
//        redisTemplate.opsForValue().set(idKey, updatedCategory, Duration.ofHours(2));
//        redisTemplate.opsForValue().set(nameKey, updatedCategory, Duration.ofHours(2));
//
//        // Invalidate pagination cache
        invalidatePaginationCache();

        return categoryMapper.convertToDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", key = "#id")
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "id", id));

        categoryRepository.delete(category);

        // Remove from cache
//        String idKey = REDIS_PREFIX_ID + id;
//        String nameKey = REDIS_PREFIX_NAME + category.getName();
//        redisTemplate.delete(idKey);
//        redisTemplate.delete(nameKey);

        // Invalidate pagination cache
        invalidatePaginationCache();
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        String key = REDIS_PREFIX_ID + id;

        // Try to get from Redis first
        CategoryDTO cachedCategory = (CategoryDTO) redisTemplate.opsForValue().get(key);
        if (cachedCategory != null) {
            return cachedCategory;
        }

        // If not in cache, fetch from database
        CategoryDTO category = categoryRepository.findById(id)
                .map(categoryMapper::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "id", id));

        // Cache the result
        redisTemplate.opsForValue().set(key, category, Duration.ofHours(2));

        return category;
    }

    @Override
    public CategoryDTO getCategoryByName(String name) {
        String key = REDIS_PREFIX_NAME + name;

        // Try to get from Redis first
        CategoryDTO cachedCategory = (CategoryDTO) redisTemplate.opsForValue().get(key);
        if (cachedCategory != null) {
            return cachedCategory;
        }

        // If not in cache, fetch from database
        CategoryDTO category = categoryRepository.findByName(name)
                .map(categoryMapper::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, "name", name));

        // Cache the result
        redisTemplate.opsForValue().set(key, category, Duration.ofHours(2));

        return category;
    }

    @Override
    public PaginationResponse<CategoryDTO> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir) {
        String key = REDIS_PREFIX_PAGE + pageNumber + ":" + pageSize + ":" + sortBy + ":" + sortDir;

        // Try to get from Redis first
        Object cachedData = redisTemplate.opsForValue().get(key);
        if (cachedData != null) {
            if (cachedData instanceof PaginationResponse) {
                return (PaginationResponse<CategoryDTO>) cachedData;
            } else if (cachedData instanceof LinkedHashMap) {
                return objectMapper.convertValue(cachedData, new TypeReference<>() {});
            }
        }

        // If not in cache, fetch from database
        Page<Category> categories = categoryRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<CategoryDTO> content = categories.getContent()
                .stream()
                .map(categoryMapper::convertToDTO)
                .toList();

        PaginationResponse<CategoryDTO> response = PaginationUtils.createPaginationResponse(content, categories);

        // Cache the result
        redisTemplate.opsForValue().set(key, response, Duration.ofHours(2));

        return response;
    }

    @Override
    public List<Category> getAllById(Set<Long> ids) {
        return categoryRepository.findAllById(ids)
                .stream()
                .toList();
    }

    // Utility method to invalidate pagination cache
    private void invalidatePaginationCache() {
        Set<String> paginationKeys = redisTemplate.keys(REDIS_PREFIX_PAGE + "*");
        if (!paginationKeys.isEmpty()) {
            redisTemplate.delete(paginationKeys);
        } else {
            log.warn("No pagination cache found");
        }
    }
}