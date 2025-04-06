package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.entity.Warehouse;
import org.khanhpham.wms.domain.mapper.WarehouseMapper;
import org.khanhpham.wms.domain.request.WarehouseRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.WarehouseRepository;
import org.khanhpham.wms.service.CacheService;
import org.khanhpham.wms.service.UserService;
import org.khanhpham.wms.service.WarehouseService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.khanhpham.wms.utils.RedisKeyUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String WAREHOUSE = "Warehouse";
    private static final String CODE = "code";
    private static final String REDIS_PREFIX_PATTERN = "warehouses:page:";
    private static final Duration REDIS_TTL = Duration.ofMinutes(30);

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final UserService userService;
    private final CacheService cacheService;

    @Override
    public WarehouseDTO createWarehouse(@NotNull WarehouseRequest warehouseRequest) {
        if (warehouseRepository.existsByName(warehouseRequest.getName())) {
            throw new ResourceAlreadyExistException(WAREHOUSE, "name", warehouseRequest.getName());
        }
        if (warehouseRepository.existsByWarehouseCode(warehouseRequest.getWarehouseCode())) {
            throw new ResourceAlreadyExistException(WAREHOUSE, "code", warehouseRequest.getWarehouseCode());
        }

        Warehouse warehouse = warehouseMapper.convertToEntity(warehouseRequest);
        WarehouseDTO savedWarehouse = warehouseMapper.convertToDTO(warehouseRepository.save(warehouse));

        cacheWarehouse(savedWarehouse);
        return savedWarehouse;
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, @NotNull WarehouseRequest warehouseRequest) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id));
        String oldCode = warehouse.getWarehouseCode();
        User manager = userService.getUser(warehouseRequest.getManagerId());
        warehouse.setName(warehouseRequest.getName());
        warehouse.setAddress(warehouseRequest.getAddress());
        warehouse.setLocation(warehouseRequest.getLocation());
        warehouse.setWarehouseCode(warehouseRequest.getWarehouseCode());
        warehouse.setDescription(warehouseRequest.getDescription());
        warehouse.setManager(manager);

        WarehouseDTO updatedWarehouse = warehouseMapper.convertToDTO(warehouseRepository.save(warehouse));

        if (!oldCode.equals(updatedWarehouse.getWarehouseCode())) {
            cacheService.evictByKeys(
                    RedisKeyUtils.generateKey(WAREHOUSE, CODE, oldCode)
            );

            cacheService.cacheValue(
                    RedisKeyUtils.generateKey(WAREHOUSE, CODE, updatedWarehouse.getWarehouseCode()),
                    updatedWarehouse.getId(),
                    REDIS_TTL
            );
        }

        cacheService.cacheValue(
                RedisKeyUtils.generateIdKey(WAREHOUSE, updatedWarehouse.getId()),
                updatedWarehouse,
                REDIS_TTL
        );
        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
        return updatedWarehouse;
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        return cacheService.getCached(
                RedisKeyUtils.generateIdKey(WAREHOUSE, id),
                new TypeReference<>() {},
                () -> warehouseMapper.convertToDTO(findById(id)),
                REDIS_TTL
        );
    }

    @Override
    public void deleteWarehouseById(Long id) throws RuntimeException {
        Warehouse warehouse = findById(id);
        warehouseRepository.delete(warehouse);
        cacheService.evictByKeys(
                RedisKeyUtils.generateIdKey(WAREHOUSE, id),
                RedisKeyUtils.generateKey(WAREHOUSE, CODE, warehouse.getWarehouseCode())
        );
        cacheService.evictByPattern("warehouses:page:*");
    }

    @Override
    public PaginationResponse<WarehouseDTO> getAllWarehouses(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir
    ) {
        String key = RedisKeyUtils.generatePatternKey(
                WAREHOUSE,
                pageNumber,
                pageSize,
                sortBy,
                sortDir
        );

        return cacheService.getCached(
                key,
                new TypeReference<>() {},
                () -> getAllWarehousesFromDB(pageNumber, pageSize, sortBy, sortDir),
                REDIS_TTL
        );
    }

    @Override
    public Warehouse getById(Long id) {
        return findById(id);
    }

    // --------- Private methods ---------
    private Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE, "id", id));
    }

    private @NotNull PaginationResponse<WarehouseDTO> getAllWarehousesFromDB(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Warehouse> warehouses = warehouseRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<WarehouseDTO> content = warehouses.getContent()
                .stream()
                .map(warehouseMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, warehouses);
    }

    private void cacheWarehouse(WarehouseDTO warehouseDTO) {
        cacheService.cacheValue(
                RedisKeyUtils.generateIdKey(WAREHOUSE, warehouseDTO.getId()),
                warehouseDTO,
                REDIS_TTL
        );

        cacheService.cacheValue(
                RedisKeyUtils.generateKey(WAREHOUSE, CODE, warehouseDTO.getWarehouseCode()),
                warehouseDTO,
                REDIS_TTL
        );

        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
    }


}
