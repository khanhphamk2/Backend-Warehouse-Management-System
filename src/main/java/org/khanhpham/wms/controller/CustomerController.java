package org.khanhpham.wms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.service.CustomerService;
import org.khanhpham.wms.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
            summary = "Get all customers",
            description = "Get all customers"
    )
    @GetMapping
    public ResponseEntity<PaginationResponse<CustomerDTO>> getAllCustomer(
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageNumber, pageSize, sortBy, sortDir));
    }

    @Operation(
            summary = "Get customer by identity",
            description = "Get customer by identity"
    )
    @GetMapping("/identity")
    public ResponseEntity<CustomerDTO> getCustomerByIdentity(
            @Parameter(description = "Identity of customer", example = "khanh pham")
            @Valid @RequestParam String identity) {
        return ResponseEntity.ok(customerService.findByIdentity(identity));
    }

    @Operation(
            summary = "Create a new customer",
            description = "Create a new customer"
    )
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the customer to be created",
                    required = true
            )
            @Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(customerRequest));
    }

    @Operation(
            summary = "Get a customer by ID",
            description = "Get a customer by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "ID of the customer to be retrieved", example = "1")
            @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @Operation(
            summary = "Update a customer",
            description = "Update a customer by the provided ID and request body data"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "ID of the customer to be updated", example = "1")
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the customer to be updated",
                    required = true
            )
            @Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerRequest));
    }

    @Operation(
            summary = "Delete a customer by ID",
            description = "Delete a customer by ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer to be deleted", example = "1")
            @PathVariable(value = "id") Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
