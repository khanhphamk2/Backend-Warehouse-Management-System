package org.khanhpham.wms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.request.ProductRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.data.rest.base-path}/products")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "Get a list of products",
            description = "Returns a list of products."
    )
    @GetMapping
    public ResponseEntity<PaginationResponse<ProductDTO>> getAllProducts(
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir));
    }

    @Operation(
            summary = "Get a product by ID",
            description = "API to get a product by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID of the product to be obtained", example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
            summary = "Create a new product",
            description = "API to create a new product with the provided request body data."
    )
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the product to be created",
                    required = true
            )
            @RequestBody ProductRequest request
    ){
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Operation(
            summary = "Update a product by ID",
            description = "API to update a product by its ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID of the product to be updated", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the product to be updated",
                    required = true
            )
            @RequestBody ProductRequest request
    ){
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @Operation(
            summary = "Delete a product by ID",
            description = "API to delete a product by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to be deleted", example = "1")
            @PathVariable Long id
    ){
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get a product by SKU",
            description = "Finds a product using its SKU."
    )
    @GetMapping("/by-price")
    public ResponseEntity<PaginationResponse<ProductDTO>> getProductsByPrice(
            @Parameter(description = "Price of the product", example = "100")
            @RequestParam(value = "price") BigDecimal price,
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return ResponseEntity.ok(
                productService.getProductsByPrice(price, pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Operation(
            summary = "Get a product by SKU",
            description = "API to get a product by its SKU."
    )
    @GetMapping("/by-sku")
    public ResponseEntity<ProductDTO> getProductBySku(
            @Parameter(description = "SKU of the product to be obtained", example = "SKU-001")
            @RequestParam String sku
    ){
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @Operation(
            summary = "Get a product by name",
            description = "API to get a product by its name."
    )
    @GetMapping("/by-name")
    public ResponseEntity<ProductDTO> getProductByName(
            @Parameter(description = "Name of the product to be obtained", example = "Product 1")
            @RequestParam String name
    ){
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @Operation(
            summary = "Get a list of products by category ID",
            description = "Returns a list of products by category ID."
    )
    @GetMapping("by-category")
    public ResponseEntity<PaginationResponse<ProductDTO>> getProductsByCategoryId(
            @Parameter(description = "ID of the category", example = "1")
            @RequestParam Long categoryId,
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(
                productService.getProductsByCategoryId(categoryId, pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Operation(
            summary = "Get a list of products by price range",
            description = "Returns a list of products by price range."
    )
    @GetMapping("/price-range")
    public ResponseEntity<PaginationResponse<ProductDTO>> getProductsByPriceRange(
            @Parameter(description = "Minimum price of the product", example = "100")
            @RequestParam(value = "min") Double min,
            @Parameter(description = "Maximum price of the product", example = "200")
            @RequestParam(value = "max") Double max,
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(
                productService.getProductsByPriceRange(min, max, pageNumber, pageSize, sortBy, sortDir)
        );
    }

    @Operation(
            summary = "Set product status",
            description = "API to set the status of a product."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductDTO> setProductStatus(
            @Parameter(description = "ID of the product to be updated", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Status of the product", example = "true")
            @RequestParam boolean status
    ) {
        return ResponseEntity.ok(productService.setProductStatus(id, status));
    }
}
