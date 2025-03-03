package org.khanhpham.wms.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.model.Category;
import org.khanhpham.wms.domain.model.Product;
import org.khanhpham.wms.domain.model.Supplier;
import org.khanhpham.wms.domain.model.Warehouse;
import org.khanhpham.wms.domain.request.ProductRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.repository.CategoryRepository;
import org.khanhpham.wms.repository.ProductRepository;
import org.khanhpham.wms.repository.SupplierRepository;
import org.khanhpham.wms.repository.WarehouseRepository;
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.service.SupplierService;
import org.khanhpham.wms.service.WarehouseService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
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
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final WarehouseService warehouseService;

    private ProductDTO convertToDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    private Product convertToEntity(ProductRequest request) {
        Supplier supplier = supplierService.getSupplierById(request.getSupplierId());
        Warehouse warehouse = warehouseService.getWarehouse(request.getWarehouseId());
        List<Category> categories = categoryService.getAllById(request.getCategoryIds());
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .expiryDate(request.getExpiryDate())
                .unit(request.getUnit())
                .imageUrl(request.getImageUrl())
                .supplier(supplier)
                .warehouse(warehouse)
                .categories(new HashSet<>(categories))
                .build();
    }

    @Override
    public ProductDTO createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new ResourceAlreadyExistException(PRODUCT, "sku", request.getSku());
        }
        Product product = convertToEntity(request);

        return convertToDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, id)));
        product.setLastModifiedDate(LocalDateTime.now());
        modelMapper.map(request, product);
        mapCategories(product, request.getCategoryIds());

        return convertToDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
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
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, sku)));
    }

    @Override
    public ProductDTO getProductByName(String name) {
        return productRepository.findByName(name)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(PRODUCT_NOT_FOUND_MESSAGE, name)));
    }

    @Override
    public PaginationResponse<ProductDTO> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );
        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByCategoryId(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByCategoriesId(categoryId,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsBySupplierId(Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findBySupplierId(supplierId,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByPrice(BigDecimal price, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByPrice(price,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(productDTOS, products);
    }

    @Override
    public PaginationResponse<ProductDTO> getProductsByPriceRange(Double min, Double max, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> products = productRepository.findByPriceBetween(BigDecimal.valueOf(min), BigDecimal.valueOf(max),
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));

        List<ProductDTO> productDTOS = products.getContent()
                .stream()
                .map(this::convertToDTO)
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
}
