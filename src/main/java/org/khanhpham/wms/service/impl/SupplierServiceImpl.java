package org.khanhpham.wms.service.impl;

import org.khanhpham.wms.domain.dto.SupplierDTO;
import org.khanhpham.wms.domain.model.Supplier;
import org.khanhpham.wms.domain.request.SupplierRequest;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.SupplierRepository;
import org.khanhpham.wms.service.SupplierService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {
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
        return modelMapper.map(request, Supplier.class);
    }

    @Override
    public SupplierDTO createSupplier(SupplierRequest request) {
        return null;
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierRequest request) {
        return null;
    }

    @Override
    public SupplierDTO getSupplier(Long id) {
        return supplierRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplierRepository.delete(supplier);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
}
