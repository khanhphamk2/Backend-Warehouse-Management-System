package org.khanhpham.wms.service.impl;

import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.model.Customer;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.CustomerRepository;
import org.khanhpham.wms.service.CustomerService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final String CUSTOMER = "Customer";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    private CustomerDTO convertToDTO(Object object) {
        return modelMapper.map(object, CustomerDTO.class);
    }

    private Customer convertToEntity(CustomerRequest customerRequest) {
        return Customer.builder()
                .address(customerRequest.getAddress())
                .email(customerRequest.getEmail())
                .name(customerRequest.getName())
                .phone(customerRequest.getPhone())
                .build();
    }

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
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "identity", identity));
    }

    @Override
    public CustomerDTO createCustomer(CustomerRequest customerRequest) {
        Customer customer = convertToEntity(customerRequest);
        try {
            Customer savedCustomer = customerRepository.save(customer);
            return convertToDTO(savedCustomer);
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

        return convertToDTO(customerRepository.save(customer));
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
                .map(this::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, customers);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));
    }
}
