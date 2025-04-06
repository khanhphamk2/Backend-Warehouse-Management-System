package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.domain.dto.SupplierDTO;
import org.khanhpham.wms.domain.entity.Supplier;
import org.khanhpham.wms.domain.mapper.SupplierMapper;
import org.khanhpham.wms.domain.request.SupplierRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.SupplierRepository;
import org.khanhpham.wms.service.CacheService;
import org.khanhpham.wms.service.SupplierService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private static final String SUPPLIER = "Supplier";
    private static final String REDIS_PREFIX_ID = "supplier:id:";
    private static final String REDIS_PREFIX_NAME = "supplier:name:";
    private static final String REDIS_PREFIX_PATTERN = "suppliers:page:";
    private static final Duration REDIS_TTL = Duration.ofHours(2);

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final CacheService cacheService;

    @Override
    public SupplierDTO createSupplier(SupplierRequest request) {
        validateCategoryExistence(request);
        Supplier supplier = supplierMapper.convertToEntity(request);
        SupplierDTO savedSupplier = save(supplier);

        updateSupplierCaches(savedSupplier);

        return savedSupplier;
    }

    @Override
    public SupplierDTO updateSupplier(Long id, @NotNull SupplierRequest request) {
        Supplier supplier = findById(id);
        String oldName = supplier.getName();

        supplier.setAddress(request.getAddress());
        supplier.setContactInfo(request.getContactInfo());
        supplier.setDescription(request.getDescription());
        supplier.setEmail(request.getEmail());
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());

        SupplierDTO updatedSupplier = save(supplier);

        if (!oldName.equals(updatedSupplier.getName())) {
            cacheService.evictByKeys(nameKey(oldName));
        }

        updateSupplierCaches(updatedSupplier);

        return updatedSupplier;
    }

    @Override
    public SupplierDTO getSupplierById(Long id) {
        return cacheService.getCached(
                idKey(id),
                new TypeReference<>() {},
                () -> supplierMapper.convertToDTO(findById(id)),
                REDIS_TTL
        );
    }

    @Override
    public SupplierDTO getSupplierByName(String name) {
        Long supplierId = cacheService.getCached(
                nameKey(name),
                new TypeReference<>() {},
                () -> findByName(name).getId(),
                REDIS_TTL
        );
        return getSupplierById(supplierId);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = findById(id);
        supplierRepository.delete(supplier);

        cacheService.evictByKeys(idKey(id));
        cacheService.evictByKeys(nameKey(supplier.getName()));
        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
    }

    @Override
    public PaginationResponse<SupplierDTO> getAllSuppliers(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        return cacheService.getCached(
                patternKey(pageNumber, pageSize, sortBy, sortDir),
                new TypeReference<>() {},
                () -> getAllSuppliersFromDB(pageNumber, pageSize, sortBy, sortDir),
                REDIS_TTL
        );
    }

    @Override
    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SUPPLIER, "id", id));
    }

    private Supplier findByName(String name) {
        return supplierRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(SUPPLIER, "name", name));
    }

    private void validateCategoryExistence(@NotNull SupplierRequest request) {
        if (supplierRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistException(SUPPLIER, "name", request.getName());
        }
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistException(SUPPLIER, "email", request.getEmail());
        }
        if (supplierRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistException(SUPPLIER, "phone", request.getPhone());
        }
    }

    private SupplierDTO save(Supplier supplier) {
        return supplierMapper.convertToDTO(supplierRepository.save(supplier));
    }

    private @NotNull PaginationResponse<SupplierDTO> getAllSuppliersFromDB(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Supplier> suppliers = supplierRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<SupplierDTO> content = suppliers.getContent()
                .stream()
                .map(supplierMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, suppliers);
    }

    private void updateSupplierCaches(@NotNull SupplierDTO supplierDTO) {
        cacheService.cacheValue(idKey(supplierDTO.getId()), supplierDTO, REDIS_TTL);
        cacheService.cacheValue(nameKey(supplierDTO.getName()), supplierDTO.getId(), REDIS_TTL);
        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
    }

    @Contract(pure = true)
    private @NotNull String idKey(Long id) {
        return REDIS_PREFIX_ID + id;
    }

    @Contract(pure = true)
    private @NotNull String nameKey(String name) {
        return REDIS_PREFIX_NAME + name;
    }

    @Contract(pure = true)
    private @NotNull String patternKey(int pageNumber, int pageSize, String sortBy, String sortDir) {
        return REDIS_PREFIX_PATTERN + pageNumber + ":" + pageSize + ":" + sortBy + ":" + sortDir;
    }
}
