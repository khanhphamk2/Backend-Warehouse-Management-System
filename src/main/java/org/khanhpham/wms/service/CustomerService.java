package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.entity.Customer;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

public interface CustomerService {
    CustomerDTO findByIdentity(String identity);
    CustomerDTO createCustomer(CustomerRequest customerRequest);
    CustomerDTO updateCustomer(Long id, CustomerRequest customerRequest);
    void deleteCustomer(Long id);
    PaginationResponse<CustomerDTO> getAllCustomers(int pageNumber, int pageSize, String sortBy, String sortDir);
    CustomerDTO getCustomerById(Long id);
    Customer findById(Long id);
}
