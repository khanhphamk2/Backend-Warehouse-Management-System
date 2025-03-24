package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.entity.Warehouse;
import org.khanhpham.wms.domain.mapper.WarehouseMapper;
import org.khanhpham.wms.domain.request.WarehouseRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.WarehouseRepository;
import org.khanhpham.wms.service.UserService;
import org.khanhpham.wms.service.WarehouseService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String WAREHOUSE = "Warehouse";
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final UserService userService;

    @Override
    public WarehouseDTO createWarehouse(WarehouseRequest warehouseRequest) {
        Warehouse warehouse = warehouseMapper.convertToEntity(warehouseRequest);
       try {
           Warehouse savedWarehouse = warehouseRepository.save(warehouse);
           return warehouseMapper.convertToDTO(savedWarehouse);
       } catch (DataIntegrityViolationException ex) {
           throw new ResourceAlreadyExistException(WAREHOUSE, "name", warehouseRequest.getName());
       }
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseRequest warehouseRequest) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id));
        User manager = userService.getUserById(warehouseRequest.getManagerId());
        warehouse.setName(StringUtils.trim(warehouseRequest.getName()));
        warehouse.setAddress(StringUtils.trim(warehouseRequest.getAddress()));
        warehouse.setLocation(StringUtils.trim(warehouseRequest.getLocation()));
        warehouse.setWarehouseCode(StringUtils.trim(warehouseRequest.getWarehouseCode()));
        warehouse.setDescription(StringUtils.trim(warehouseRequest.getDescription()));
        warehouse.setManager(manager);
        return warehouseMapper.convertToDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .map(warehouseMapper::convertToDTO)
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
                .map(warehouseMapper::convertToDTO)
                .toList();
        return PaginationUtils.createPaginationResponse(content, warehouses);
    }

    @Override
    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id));
    }
}
