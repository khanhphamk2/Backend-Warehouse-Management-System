package org.khanhpham.wms.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.entity.Warehouse;
import org.khanhpham.wms.domain.request.WarehouseRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.WarehouseRepository;
import org.khanhpham.wms.service.UserService;
import org.khanhpham.wms.service.WarehouseService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private static final String WAREHOUSE = "Warehouse";
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper, UserService userService) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    private WarehouseDTO convertToDTO(Warehouse warehouse) {
        return modelMapper.map(warehouse, WarehouseDTO.class);
    }

    private Warehouse convertToEntity(WarehouseRequest warehouseRequest) {
        return Warehouse.builder()
                .name(StringUtils.trim(warehouseRequest.getName()))
                .address(StringUtils.trim(warehouseRequest.getAddress()))
                .warehouseCode(StringUtils.trim(warehouseRequest.getWarehouseCode()))
                .location(StringUtils.trim(warehouseRequest.getLocation()))
                .manager(getManagerFromId(warehouseRequest.getManagerId()))
                .description(StringUtils.trim(warehouseRequest.getDescription()))
                .build();
    }

    private User getManagerFromId(Long id) {
        try {
            return modelMapper.map(userService.findById(id), User.class);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Management", "id", id);
        }
    }


    @Override
    public WarehouseDTO createWarehouse(WarehouseRequest warehouseRequest) {
        Warehouse warehouse = convertToEntity(warehouseRequest);
       try {
           Warehouse savedWarehouse = warehouseRepository.save(warehouse);
           return convertToDTO(savedWarehouse);
       } catch (DataIntegrityViolationException ex) {
           throw new ResourceAlreadyExistException(WAREHOUSE, "name", warehouseRequest.getName());
       }
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseRequest warehouseRequest) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id));
        warehouse.setName(StringUtils.trim(warehouseRequest.getName()));
        warehouse.setAddress(StringUtils.trim(warehouseRequest.getAddress()));
        warehouse.setLocation(StringUtils.trim(warehouseRequest.getLocation()));
        warehouse.setWarehouseCode(StringUtils.trim(warehouseRequest.getWarehouseCode()));
        warehouse.setDescription(StringUtils.trim(warehouseRequest.getDescription()));
        warehouse.setManager(getManagerFromId(warehouseRequest.getManagerId()));
        return convertToDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id));
    }

    @Override
    public void deleteWarehouseById(Long id) throws RuntimeException {
        warehouseRepository.delete(
                warehouseRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id)));
    }

    @Override
    public PaginationResponse<WarehouseDTO> getAllWarehouses(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Warehouse> warehouses = warehouseRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );
        List<WarehouseDTO> content = warehouses.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(content, warehouses);
    }

    @Override
    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id));
    }
}
