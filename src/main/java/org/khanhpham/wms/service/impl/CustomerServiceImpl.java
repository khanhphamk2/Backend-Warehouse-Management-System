package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.entity.Customer;
import org.khanhpham.wms.domain.mapper.CustomerMapper;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.CustomerRepository;
import org.khanhpham.wms.service.CacheService;
import org.khanhpham.wms.service.CustomerService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private static final String CUSTOMER = "Customer";
    private static final String REDIS_PREFIX_IDENTITY = "customer:identity:";
    private static final String REDIS_PREFIX_ID = "customer:id:";
    private static final String REDIS_PREFIX_NAME = "customer:name:";
    private static final String REDIS_PATTERN_ALL = "customers:page:";
    private static final Duration REDIS_TTL = Duration.ofHours(1);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CacheService cacheService;

    @Override
    public CustomerDTO findByIdentity(String identity) {
        String identityKey = identityKey(identity);

        Long customerId = cacheService.getCachedValue(identityKey, new TypeReference<>() {});
        if (customerId != null) {
            return getCustomerFromCache(idKey(customerId), () -> findByEmailOrPhone(identity));
        }

        CustomerDTO customerDTO = findByEmailOrPhone(identity);
        cacheService.cacheValue(identityKey, customerDTO.getId(), REDIS_TTL);
        cacheService.cacheValue(idKey(customerDTO.getId()), customerDTO, REDIS_TTL);

        return customerDTO;
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(@NotNull CustomerRequest customerRequest) {
        validateCustomerExistence(customerRequest.getName(), customerRequest.getPhone());
        Customer customer = customerMapper.convertToEntity(customerRequest);
        CustomerDTO savedCustomer = save(customer);

        saveToCache(savedCustomer);
        clearAllCustomersCache();

        return savedCustomer;
    }

    @Override
    public CustomerDTO updateCustomer(Long id, @NotNull CustomerRequest customerRequest) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));
        String oldName = customer.getName();

        customer.setAddress(customerRequest.getAddress());
        customer.setEmail(customerRequest.getEmail());
        customer.setName(customerRequest.getName());
        customer.setPhone(customerRequest.getPhone());

        CustomerDTO updated = save(customer);
        if (!oldName.equals(updated.getName())) {
            cacheService.evictByKeys(nameKey(oldName));
        }
        saveToCache(updated);
        clearAllCustomersCache();

        return updated;
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
        cacheService.evictByKeys(
                idKey(id),
                nameKey(customer.getName()),
                identityKey(customer.getEmail()),
                identityKey(customer.getPhone())
        );
        clearAllCustomersCache();
    }

    @Override
    public PaginationResponse<CustomerDTO> getAllCustomers(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        String cacheKey = REDIS_PATTERN_ALL + pageNumber + ":" + pageSize + ":" + sortBy + ":" + sortDir;
        return cacheService.getCached(cacheKey, new TypeReference<>() {},
                () -> getCustomers(pageNumber, pageSize, sortBy, sortDir), REDIS_TTL);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return getCustomerFromCache(idKey(id), () -> customerMapper.convertToDTO(findById(id)));
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "id", id));
    }

    // ----- Helper methods -----

    private CustomerDTO save(Customer customer) {
        return customerMapper.convertToDTO(customerRepository.save(customer));
    }

    private @NotNull PaginationResponse<CustomerDTO> getCustomers(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Customer> customers = customerRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir));
        List<CustomerDTO> content = customers.getContent().stream()
                .map(customerMapper::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(content, customers);
    }

    private CustomerDTO findByEmailOrPhone(String identity) {
        return customerRepository.findByEmailOrPhone(identity, identity)
                .or(() -> parseId(identity).flatMap(customerRepository::findById))
                .map(customerMapper::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER, "identity", identity));
    }

    private Optional<Long> parseId(String identity) {
        try {
            return Optional.of(Long.parseLong(identity));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void validateCustomerExistence(String email, String phone) {
        if (customerRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistException(CUSTOMER, "email", email);
        } else if (customerRepository.existsByPhone(phone)) {
            throw new ResourceAlreadyExistException(CUSTOMER, "phone", phone);
        }
    }

    private CustomerDTO getCustomerFromCache(String key, Supplier<CustomerDTO> dbSupplier) {
        return cacheService.getCached(key, new TypeReference<>() {}, dbSupplier, REDIS_TTL);
    }

    private void saveToCache(@NotNull CustomerDTO dto) {
        cacheService.cacheValue(idKey(dto.getId()), dto, REDIS_TTL);
        cacheService.cacheValue(nameKey(dto.getName()), dto.getId(), REDIS_TTL);
        cacheService.cacheValue(identityKey(dto.getEmail()), dto.getId(), REDIS_TTL);
        cacheService.cacheValue(identityKey(dto.getPhone()), dto.getId(), REDIS_TTL);
    }

    private void clearAllCustomersCache() {
        cacheService.evictByPattern(REDIS_PATTERN_ALL + "*");
    }

    // ----- Cache Key Helpers -----
    @Contract(pure = true)
    private @NotNull String idKey(Long id) {
        return REDIS_PREFIX_ID + id;
    }

    @Contract(pure = true)
    private @NotNull String nameKey(String name) {
        return REDIS_PREFIX_NAME + name;
    }

    @Contract(pure = true)
    private @NotNull String identityKey(String identity) {
        return REDIS_PREFIX_IDENTITY + identity;
    }
}
