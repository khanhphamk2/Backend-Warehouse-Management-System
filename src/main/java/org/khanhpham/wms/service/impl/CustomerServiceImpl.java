package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.entity.Customer;
import org.khanhpham.wms.domain.mapper.CustomerMapper;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.CustomerRepository;
import org.khanhpham.wms.service.CustomerService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private static final String CUSTOMER = "Customer";
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerDTO findByIdentity(String identity) {
        return customerRepository.findByEmailOrPhone(identity, identity)
                .or(() -> {
                    try {
                        Long id = Long.parseLong(identity);
                        return customerRepository.findById(id);
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                })
                .map(customerMapper::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "identity", identity));
    }

    @Override
    public CustomerDTO createCustomer(CustomerRequest customerRequest) {
        Customer customer = customerMapper.convertToEntity(customerRequest);
        try {
            Customer savedCustomer = customerRepository.save(customer);
            return customerMapper.convertToDTO(savedCustomer);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Email or Phone already exists");
        }
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerRequest customerRequest) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));

        customer.setAddress(customerRequest.getAddress());
        customer.setEmail(customerRequest.getEmail());
        customer.setName(customerRequest.getName());
        customer.setPhone(customerRequest.getPhone());

        return customerMapper.convertToDTO(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));
        customerRepository.delete(customer);
    }

    @Override
    public PaginationResponse<CustomerDTO> getAllCustomers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Customer> customers = customerRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );
        List<CustomerDTO> content = customers.getContent()
                .stream()
                .map(customerMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, customers);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));
    }
}
