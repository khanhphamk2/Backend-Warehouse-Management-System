package org.khanhpham.wms.service.impl;

import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.model.User;
import org.khanhpham.wms.domain.model.Warehouse;
import org.khanhpham.wms.domain.request.WarehouseRequest;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.WarehouseRepository;
import org.khanhpham.wms.service.UserService;
import org.khanhpham.wms.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class WarehouseServiceImpl implements WarehouseService {
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
                .name(warehouseRequest.getName())
                .address(warehouseRequest.getAddress())
                .warehouseCode(warehouseRequest.getWarehouseCode())
                .location(warehouseRequest.getLocation())
                .manager(modelMapper.map(userService.findById(warehouseRequest.getManagerId()), User.class))
                .description(warehouseRequest.getDescription())
                .build();
    }

    @Override
    public WarehouseDTO createWarehouse(WarehouseRequest warehouseRequest) {
        Warehouse warehouse = convertToEntity(warehouseRequest);
       try {
           Warehouse savedWarehouse = warehouseRepository.save(warehouse);
           return convertToDTO(savedWarehouse);
       } catch (Exception ex) {
           throw new ResourceAlreadyExistException("Warehouse", "name", warehouseRequest.getName());
       }
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseRequest warehouseRequest) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouse.setName(warehouseRequest.getName());
        warehouse.setAddress(warehouseRequest.getAddress());
        warehouse.setLocation(warehouseRequest.getLocation());
        warehouse.setWarehouseCode(warehouseRequest.getWarehouseCode());
        warehouse.setDescription(warehouseRequest.getDescription());
        warehouse.setManager(modelMapper.map(userService.findById(warehouseRequest.getManagerId()), User.class));
        return convertToDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
    }

    @Override
    public void deleteWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouseRepository.delete(warehouse);
    }
}
