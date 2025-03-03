package org.khanhpham.wms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.dto.SupplierDTO;
import org.khanhpham.wms.domain.request.SupplierRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.service.SupplierService;
import org.khanhpham.wms.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/suppliers")
public class SupplierController {
    private final SupplierService supplierService;
    private final ProductService productService;

    public SupplierController(SupplierService supplierService, ProductService productService) {
        this.supplierService = supplierService;
        this.productService = productService;
    }

    @Operation(
            summary = "Get a list of suppliers",
            description = "Returns a paginated list of suppliers with sorting and filtering options."
    )
//    @ApiResponse(responseCode = "200", description = "Successfully retrieved supplier list",
//            content = @Content(mediaType = "application/json"))
//    @ApiResponse(responseCode = "400", description = "Invalid parameters provided")
//    @ApiResponse(responseCode = "500", description = "Server error")
    @GetMapping
    public ResponseEntity<PaginationResponse<SupplierDTO>> getAllSuppliers(
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(pageNumber, pageSize, sortBy, sortDir));
    }

    @Operation(
            summary = "Create a new supplier",
            description = "API to create a new supplier with the provided request body data."
    )
    @ApiResponse(responseCode = "200", description = "Successfully created a supplier",
            content = @Content(schema = @Schema(implementation = SupplierDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid data provided")
    @ApiResponse(responseCode = "500", description = "Server error")
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the supplier to be created",
                    required = true
            )
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.createSupplier(request));
    }

    @Operation(
            summary = "Update a supplier by id",
            description = "API to update a supplier with the provided id and request body data"
    )
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @Parameter(description = "Id of the supplier to be updated", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the supplier to be updated",
                    required = true
            )
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @Operation(
            summary = "Get a supplier by id",
            description = "API to get a supplier by the provided id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplier(
            @Parameter(description = "Id of the supplier to be retrieved", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplier(id));
    }

    @Operation(
            summary = "Delete a supplier by id",
            description = "API to delete a supplier by the provided id"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "Id of the supplier to be deleted", example = "1")
            @PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get products by supplier ID",
            description = "API to get a paginated list of products by the provided supplier ID"
    )
    @GetMapping("/{id}/products")
    public ResponseEntity<PaginationResponse<ProductDTO>> getProductsBySupplierId(
            @Parameter(description = "ID of the supplier", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(productService.getProductsBySupplierId(id, pageNumber, pageSize, sortBy, sortDir));
    }
}
