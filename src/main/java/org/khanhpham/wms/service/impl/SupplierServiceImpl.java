package org.khanhpham.wms.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.khanhpham.wms.domain.dto.SupplierDTO;
import org.khanhpham.wms.domain.model.Supplier;
import org.khanhpham.wms.domain.request.SupplierRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.SupplierRepository;
import org.khanhpham.wms.service.SupplierService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SupplierServiceImpl implements SupplierService {
    private static final String SUPPLIER = "Supplier";
    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    public SupplierServiceImpl(SupplierRepository supplierRepository, ModelMapper modelMapper) {
        this.supplierRepository = supplierRepository;
        this.modelMapper = modelMapper;
    }

    private SupplierDTO convertToDTO(Supplier supplier) {
        return modelMapper.map(supplier, SupplierDTO.class);
    }

    private Supplier convertToEntity(SupplierRequest request) {
        return Supplier.builder()
                .name(StringUtils.trim(request.getName()))
                .contactInfo(StringUtils.trim(request.getContactInfo()))
                .address(StringUtils.trim(request.getAddress()))
                .phone(StringUtils.trim(request.getPhone()))
                .email(StringUtils.trim(request.getEmail()))
                .description(StringUtils.trim(request.getDescription()))
                .purchaseOrders(new ArrayList<>())
                .build();
    }

    @Override
    public SupplierDTO createSupplier(SupplierRequest request) {
        Supplier supplier = convertToEntity(request);
        try {
            Supplier savedSupplier = supplierRepository.save(supplier);
            return convertToDTO(savedSupplier);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error occurred while saving supplier: {}", ex.getMessage(), ex);
            throw new ResourceAlreadyExistException(SUPPLIER, "name", request.getName());
        }
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SUPPLIER, "id", id));
        try {
            // Trim and update fields using Optional to avoid NullPointerException
            supplier.setAddress(Optional.ofNullable(request.getAddress()).map(StringUtils::trim).orElse(null));
            supplier.setContactInfo(Optional.ofNullable(request.getContactInfo()).map(StringUtils::trim).orElse(null));
            supplier.setDescription(Optional.ofNullable(request.getDescription()).map(StringUtils::trim).orElse(null));
            supplier.setEmail(Optional.ofNullable(request.getEmail()).map(StringUtils::trim).orElse(null));
            supplier.setName(Optional.ofNullable(request.getName()).map(StringUtils::trim).orElse(null));
            supplier.setPhone(Optional.ofNullable(request.getPhone()).map(StringUtils::trim).orElse(null));

            return convertToDTO(supplierRepository.save(supplier));
        } catch (RuntimeException ex) {
            throw new RuntimeException("Error occurred while updating supplier: " + ex.getMessage());
        }
    }

    @Override
    public SupplierDTO getSupplier(Long id) {
        return supplierRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(SUPPLIER, "id", id));
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SUPPLIER, "id", id));
        supplierRepository.delete(supplier);
    }

    @Override
    public PaginationResponse<SupplierDTO> getAllSuppliers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Supplier> suppliers = supplierRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );
        List<SupplierDTO> content = suppliers.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(content, suppliers);
    }

    @Override
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SUPPLIER, "id", id));
    }

}
