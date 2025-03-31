package org.khanhpham.wms.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.entity.Category;
import org.khanhpham.wms.domain.entity.Product;
import org.khanhpham.wms.domain.mapper.ProductMapper;
import org.khanhpham.wms.domain.request.ProductRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.repository.ProductRepository;
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product {0} not found";
    private static final String PRODUCT = "Product";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    @Override
    public ProductDTO createProduct(@NotNull ProductRequest request) {
        if (Boolean.FALSE.equals(productRepository.existsBySku(request.getSku()))) {
            Product product = productMapper.convertToEntity(request);

            return productMapper.convertToDTO(productRepository.save(product));
        } else {
            throw new ResourceAlreadyExistException(PRODUCT, "sku", request.getSku());
        }
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, id)));
        product.setLastModifiedDate(LocalDateTime.now());
        productMapper.map(request, product);
        mapCategories(product, request.getCategoryIds());

        return productMapper.convertToDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, id)));
    }

    @Override
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, id));
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(productMapper::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, sku)));
    }

    @Override
    public ProductDTO getProductByName(String name) {
        return productRepository.findByName(name)
                .map(productMapper::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, name)));
    }

    @Override
    public PaginationResponse<ProductDTO> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );
        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByCategoryId(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByCategoriesId(categoryId,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsBySupplierId(Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findBySupplierId(supplierId,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByPrice(BigDecimal price, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByPrice(price,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByPriceRange(Double min, Double max, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByPriceBetween(BigDecimal.valueOf(min), BigDecimal.valueOf(max),
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(productMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

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

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, id)));
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }
}
