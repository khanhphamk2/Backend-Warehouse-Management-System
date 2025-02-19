package org.khanhpham.wms.controller;

import jakarta.validation.Valid;
import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.khanhpham.wms.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/identity")
    public ResponseEntity<CustomerDTO> getCustomerByIdentity(String identity) {
        return ResponseEntity.ok(customerService.findByIdentity(identity));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(customerRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
}
