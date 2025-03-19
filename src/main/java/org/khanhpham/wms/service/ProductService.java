package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.entity.Product;
import org.khanhpham.wms.domain.request.ProductRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

import java.math.BigDecimal;

public interface ProductService {
    ProductDTO createProduct(ProductRequest productRequest);
    ProductDTO updateProduct(Long id, ProductRequest productRequest);
    ProductDTO getProductById(Long id);
    void deleteProductById(Long id);
    ProductDTO getProductBySku(String sku);
    ProductDTO getProductByName(String name);
    PaginationResponse<ProductDTO> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<ProductDTO> getProductsByCategoryId(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<ProductDTO> getProductsBySupplierId(Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<ProductDTO> getProductsByPrice(BigDecimal price, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<ProductDTO> getProductsByPriceRange(Double min, Double max, int pageNumber, int pageSize, String sortBy, String sortDir);
    Product findById(Long id);
    void save(Product product);
}
