package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.request.RoleRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.domain.response.RoleResponse;

public interface RoleService {
    PaginationResponse<RoleResponse> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);
    RoleResponse getRole(Long id);
    RoleResponse createRole(RoleRequest role);
    void deleteRole(Long id);
}
