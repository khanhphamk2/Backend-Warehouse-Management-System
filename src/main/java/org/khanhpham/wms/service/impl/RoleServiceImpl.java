package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.khanhpham.wms.domain.entity.Role;
import org.khanhpham.wms.domain.mapper.RoleMapper;
import org.khanhpham.wms.domain.request.RoleRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.domain.response.RoleResponse;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.RoleRepository;
import org.khanhpham.wms.service.RoleService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public PaginationResponse<RoleResponse> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Role> roles = roleRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortDir, sortBy)
        );

        List<RoleResponse> content = roles.getContent()
                .stream()
                .map(roleMapper::convertToResponse)
                .toList();

        return PaginationUtils.createPaginationResponse(content, roles);
    }

    @Override
    public RoleResponse getRole(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::convertToResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Role", "id", id));
    }

    @Override
    public RoleResponse createRole(RoleRequest role) {
        Role newRole = roleRepository.save(roleMapper.convertToEntity(role));
        return roleMapper.convertToResponse(newRole);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
