package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.entity.Category;
import org.khanhpham.wms.domain.entity.Product;
import org.khanhpham.wms.domain.mapper.ProductMapper;
import org.khanhpham.wms.domain.request.ProductRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.repository.ProductRepository;
import org.khanhpham.wms.service.CacheService;
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.khanhpham.wms.utils.RedisKeyUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product {0} not found";
    private static final String PRODUCT = "Product";
    private static final String SKU = "sku";
    private static final String NAME = "name";
    private static final String REDIS_PREFIX_PATTERN = "products:page:";
    private static final Duration REDIS_TTL = Duration.ofHours(2);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final CacheService cacheService;

    @Override
    public ProductDTO createProduct(@NotNull ProductRequest request) {
        validateProductExistence(request.getSku(), request.getName());
        Product product = productMapper.convertToEntity(request);
        ProductDTO savedProduct = save(product);
        cacheProduct(savedProduct);
        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
        return savedProduct;
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductRequest request) {
        Product product = findById(id);
        product.setLastModifiedDate(LocalDateTime.now());
        productMapper.map(request, product);
        mapCategories(product, request.getCategoryIds());
        String oldSku = product.getSku();
        String oldName = product.getName();

        evictOldCacheIfNecessary(oldSku, oldName, request);

        ProductDTO savedProduct = save(product);
        cacheProduct(savedProduct);
        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
        return savedProduct;
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return getOrCache(
                RedisKeyUtils.generateIdKey(PRODUCT, id),
                new TypeReference<>() {},
                () -> productMapper.convertToDTO(findById(id))
        );
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, id));
        }

        Product product = findById(id);

        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
        evictProductCache(productMapper.convertToDTO(product));

        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public @NotNull ProductDTO save(Product product) {
        return productMapper.convertToDTO(productRepository.save(product));
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, id)));
    }

    @Override
    public ProductDTO getProductBySku(String sku) {
        Long productId = getOrCache(
                RedisKeyUtils.generateKey(PRODUCT, SKU, sku),
                new TypeReference<>() {},
                () -> findBySku(sku).getId()
        );
        return getProductById(productId);
    }

    @Override
    public ProductDTO getProductByName(String name) {
        Long productId = getOrCache(
                RedisKeyUtils.generateKey(PRODUCT, NAME, name),
                new TypeReference<>() {},
                () -> findByName(name).getId()
        );
        return getProductById(productId);
    }

    @Override
    @Transactional
    public ProductDTO setProductStatus(Long id, boolean status) {
        Product product = findById(id);

        if (product.isActive() == status) {
            log.info("{} product with id {}", status ? "Activate" : "Deactivate", id);
            return productMapper.convertToDTO(product);
        }

        product.setActive(status);
        ProductDTO updated = save(product);
        cacheService.cacheValue(RedisKeyUtils.generateIdKey(PRODUCT, id), updated, REDIS_TTL);
        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
        return updated;
    }

    @Override
    public PaginationResponse<ProductDTO> getAllProducts(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        return getOrCache(
                RedisKeyUtils.generatePatternKey("products", pageNumber, pageSize, sortBy, sortDir),
                new TypeReference<>() {},
                () -> getAllProductsFromDB(pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByCategoryId(
            Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        return getOrCache(
                RedisKeyUtils.generatePatternKey(
                        "products:category:" + categoryId,
                        pageNumber, pageSize, sortBy, sortDir),
                new TypeReference<>() {},
                () -> getProductsByCategoryIdFromDB(categoryId, pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsBySupplierId(
            Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        return getOrCache(
                RedisKeyUtils.generatePatternKey(
                        "products:supplier:" + supplierId,
                        pageNumber, pageSize, sortBy, sortDir),
                new TypeReference<>() {},
                () -> getProductsBySupplierIdFromDB(supplierId, pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByPrice(
            BigDecimal price, int pageNumber, int pageSize, String sortBy, String sortDir) {
        return getOrCache(
                RedisKeyUtils.generatePatternKey(
                        "products:price:" + price,
                        pageNumber, pageSize, sortBy, sortDir),
                new TypeReference<>() {},
                () -> getProductsByPriceFromDB(price, pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByPriceRange(
            Double min, Double max, int pageNumber, int pageSize, String sortBy, String sortDir) {
        return getOrCache(
                RedisKeyUtils.generatePatternKey(
                        "products:price:range:" + min + ":" + max,
                        pageNumber, pageSize, sortBy, sortDir),
                new TypeReference<>() {},
                () -> getProductsByPriceRangeFromDB(min, max, pageNumber, pageSize, sortBy, sortDir)
        );
    }

    // ---------- Helper methods ----------

    @SuppressWarnings("unchecked")
    private <T> Iterable<T> castToIterableOfLong(Object object) {
        return (Iterable<T>) object;
    }

    private void mapCategories(Product product, Object categoryIds) {
        if (!(categoryIds instanceof Iterable)) {
            throw new IllegalArgumentException("categoryIds must be an iterable of Long");
        }

        Iterable<Long> ids = castToIterableOfLong(categoryIds);
        Set<Category> newCategories = new HashSet<>(categoryService.getAllById(
                StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toSet()))
        );

        if (product.getCategories() == null) {
            product.setCategories(new HashSet<>());
        }

        product.getCategories().retainAll(newCategories);
        product.getCategories().addAll(newCategories);
    }

    private Product findBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, sku)));
    }

    private Product findByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, name)));
    }

    private @NotNull PaginationResponse<ProductDTO> getAllProductsFromDB(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<ProductDTO> content = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, products);
    }

    private @NotNull PaginationResponse<ProductDTO> getProductsBySupplierIdFromDB(
            Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findBySupplierId(supplierId,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> content = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, products);
    }

    private @NotNull PaginationResponse<ProductDTO> getProductsByCategoryIdFromDB(
            Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByCategoriesId(categoryId,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> content = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, products);
    }

    private @NotNull PaginationResponse<ProductDTO> getProductsByPriceFromDB(
            BigDecimal price, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByPrice(price,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> content = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, products);
    }

    private @NotNull PaginationResponse<ProductDTO> getProductsByPriceRangeFromDB(
            Double min, Double max, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByPriceBetween(BigDecimal.valueOf(min), BigDecimal.valueOf(max),
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> content = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, products);
    }

    private void validateProductExistence(String sku, String name) {
        if (Boolean.TRUE.equals(productRepository.existsBySku(sku))) {
            throw new ResourceAlreadyExistException(PRODUCT, SKU, sku);
        }
        if (productRepository.existsByName(name)) {
            throw new ResourceAlreadyExistException(PRODUCT, NAME, name);
        }
    }

    private <T> T getOrCache(String key, TypeReference<T> typeReference, Supplier<T> dbSupplier) {
        return cacheService.getCached(key, typeReference, dbSupplier, REDIS_TTL);
    }

    private void cacheProduct(@NotNull ProductDTO productDTO) {
        cacheService.cacheValue(
                RedisKeyUtils.generateIdKey(PRODUCT, productDTO.getId()),
                productDTO,
                REDIS_TTL
        );
        cacheService.cacheValue(
                RedisKeyUtils.generateKey(PRODUCT, NAME, productDTO.getName()),
                productDTO.getId(),
                REDIS_TTL
        );
        cacheService.cacheValue(
                RedisKeyUtils.generateKey(PRODUCT, SKU, productDTO.getSku()),
                productDTO,
                REDIS_TTL
        );
    }

    private void evictProductCache(@NotNull ProductDTO productDTO) {
        List<String> keys = new ArrayList<>();
        keys.add(RedisKeyUtils.generateIdKey(PRODUCT, productDTO.getId()));
        keys.addAll(buildAliasKeys(productDTO));
        cacheService.evictByKeys(keys.toArray(String[]::new));
    }

    @Contract("_ -> new")
    private @NotNull @Unmodifiable List<String> buildAliasKeys(@NotNull ProductDTO productDTO) {
        return List.of(
                RedisKeyUtils.generateKey(PRODUCT, NAME, productDTO.getName()),
                RedisKeyUtils.generateKey(PRODUCT, SKU, productDTO.getSku())
        );
    }

    private void evictOldCacheIfNecessary(
            @NotNull String oldSku,
            String oldName,
            @NotNull ProductRequest request
    ) {
        if (!oldSku.equals(request.getSku())) {
            cacheService.evictByKeys(RedisKeyUtils.generateKey(PRODUCT, SKU, oldSku));
        }
        if (!oldName.equals(request.getName())) {
            cacheService.evictByKeys(RedisKeyUtils.generateKey(PRODUCT, NAME, oldName));
        }
    }

}
