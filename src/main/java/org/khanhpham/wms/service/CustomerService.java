package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.request.CustomerRequest;

import java.util.List;

public interface CustomerService {
    CustomerDTO findByIdentity(String identity);
    CustomerDTO createCustomer(CustomerRequest customerRequest);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    void deleteCustomer(Long id);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(Long id);
}
