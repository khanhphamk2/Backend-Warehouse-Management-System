package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.model.Warehouse;
import org.khanhpham.wms.domain.request.WarehouseRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

public interface WarehouseService {
    WarehouseDTO createWarehouse(WarehouseRequest warehouseRequest);
    WarehouseDTO updateWarehouse(Long id, WarehouseRequest warehouseRequest);
    WarehouseDTO getWarehouseById(Long id);
    void deleteWarehouseById(Long id);
    PaginationResponse<WarehouseDTO> getAllWarehouses(int pageNumber, int pageSize, String sortBy, String sortDir);
    Warehouse getWarehouse(Long id);
}
