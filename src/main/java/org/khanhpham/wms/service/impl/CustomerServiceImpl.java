package org.khanhpham.wms.service.impl;

import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.model.Customer;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.CustomerRepository;
import org.khanhpham.wms.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
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
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "identity", identity));
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
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customer.setAddress(customerDTO.getAddress());
        customer.setEmail(customerDTO.getEmail());
        customer.setName(customerDTO.getName());
        customer.setPhone(customerDTO.getPhone());

        return convertToDTO(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customerRepository.delete(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }
}
