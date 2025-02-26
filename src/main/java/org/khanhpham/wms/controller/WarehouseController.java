package org.khanhpham.wms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.request.WarehouseRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.service.WarehouseService;
import org.khanhpham.wms.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Operation(
            summary = "Get a list of warehouses",
            description = "Returns a list of warehouses."
    )
    @GetMapping
    public ResponseEntity<PaginationResponse<WarehouseDTO>> getAllWarehouses(
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(warehouseService.getAllWarehouses(pageNumber, pageSize, sortBy, sortDir));
    }

    @Operation(
            summary = "Get a warehouse by ID",
            description = "API to get a warehouse by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDTO> getWarehouseById(
            @Parameter(description = "ID of the warehouse to be obtained", example = "1")
            @PathVariable Long id){
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @Operation(
            summary = "Create a new warehouse",
            description = "API to create a new warehouse with the provided request body data."
    )
    @PostMapping
    public ResponseEntity<WarehouseDTO> createWarehouse(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the warehouse to be created",
                    required = true
            )
            @RequestBody WarehouseRequest warehouseRequest){
        return ResponseEntity.ok(warehouseService.createWarehouse(warehouseRequest));
    }

    @Operation(
            summary = "Update a warehouse",
            description = "API to update a warehouse with the provided request body data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDTO> updateWarehouse(
            @Parameter(description = "ID of the warehouse to be updated", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the warehouse to be updated",
                    required = true
            )
            @RequestBody WarehouseRequest warehouseRequest){
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseRequest));
    }

    @Operation(
            summary = "Delete a warehouse",
            description = "API to delete a warehouse by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(
            @Parameter(description = "ID of the warehouse to be deleted", example = "1")
            @PathVariable Long id){
        warehouseService.deleteWarehouseById(id);
        return ResponseEntity.noContent().build();
    }
}
