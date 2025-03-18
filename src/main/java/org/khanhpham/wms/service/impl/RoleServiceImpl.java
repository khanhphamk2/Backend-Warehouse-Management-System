package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.khanhpham.wms.domain.model.Role;
import org.khanhpham.wms.domain.request.RoleRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.domain.response.RoleResponse;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.RoleRepository;
import org.khanhpham.wms.service.RoleService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    private RoleResponse convertToResponse(Role role) {
        return modelMapper.map(role, RoleResponse.class);
    }

    private Role convertToEntity(RoleRequest role) {
        return modelMapper.map(role, Role.class);
    }

    @Override
    public PaginationResponse<RoleResponse> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Role> roles = roleRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortDir, sortBy)
        );

        List<RoleResponse> content = roles.getContent()
                .stream()
                .map(this::convertToResponse)
                .toList();

        return PaginationUtils.createPaginationResponse(content, roles);
    }

    @Override
    public RoleResponse getRole(Long id) {
        return roleRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Role", "id", id));
    }

    @Override
    public RoleResponse createRole(RoleRequest role) {
        Role newRole = roleRepository.save(convertToEntity(role));
        return convertToResponse(newRole);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
