package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.khanhpham.wms.domain.entity.Role;
import org.khanhpham.wms.domain.request.RoleRequest;
import org.khanhpham.wms.domain.response.RoleResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RoleMapper {
    private final ModelMapper modelMapper;

    public RoleResponse convertToResponse(Role role) {
        return modelMapper.map(role, RoleResponse.class);
    }

    public Role convertToEntity(RoleRequest role) {
        return modelMapper.map(role, Role.class);
    }
}
