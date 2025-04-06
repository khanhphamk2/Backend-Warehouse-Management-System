package org.khanhpham.wms.domain.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.entity.Warehouse;
import org.khanhpham.wms.domain.request.WarehouseRequest;
import org.khanhpham.wms.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarehouseMapper {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public WarehouseDTO convertToDTO(Warehouse warehouse) {
        return modelMapper.map(warehouse, WarehouseDTO.class);
    }

    public Warehouse convertToEntity(@NotNull WarehouseRequest warehouseRequest) {
        User manager = userService.getUser(warehouseRequest.getManagerId());
        return Warehouse.builder()
                .name(StringUtils.trim(warehouseRequest.getName()))
                .address(StringUtils.trim(warehouseRequest.getAddress()))
                .warehouseCode(StringUtils.trim(warehouseRequest.getWarehouseCode()))
                .location(StringUtils.trim(warehouseRequest.getLocation()))
                .manager(manager)
                .description(StringUtils.trim(warehouseRequest.getDescription()))
                .build();
    }
}
