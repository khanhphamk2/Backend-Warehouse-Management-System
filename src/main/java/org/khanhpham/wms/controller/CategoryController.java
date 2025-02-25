package org.khanhpham.wms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.khanhpham.wms.domain.dto.CategoryDTO;
import org.khanhpham.wms.domain.request.CategoryRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Get a list of categories",
            description = "Returns a list of categories."
    )
    @GetMapping
    public ResponseEntity<PaginationResponse<CategoryDTO>> getAllCategories(
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir

    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortDir));
    }

    @Operation(
            summary = "Create a new category",
            description = "API to create a new category with the provided request body data."
    )
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the category to be created",
                    required = true
            )
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @Operation(
            summary = "Get a category by ID",
            description = "API to get a category by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "ID of the category to be retrieved", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(
            summary = "Update a category by ID",
            description = "API to update a category with the provided ID and request body data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "ID of the category to be updated", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the category to be updated",
                    required = true
            )
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @Operation(
            summary = "Delete a category by ID",
            description = "API to delete a category by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID of the category to be deleted", example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Search a category by name",
            description = "API to search a category by its name."
    )
    @GetMapping("/name")
    public ResponseEntity<CategoryDTO> searchCategory(
            @Parameter(description = "Name of the category to be searched", example = "Electronics")
            @RequestParam String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }
}
