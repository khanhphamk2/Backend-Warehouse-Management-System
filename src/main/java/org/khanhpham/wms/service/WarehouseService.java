package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.WarehouseDTO;
import org.khanhpham.wms.domain.request.WarehouseRequest;

public interface WarehouseService {
    WarehouseDTO createWarehouse(WarehouseRequest warehouseRequest);
    WarehouseDTO updateWarehouse(Long id, WarehouseRequest warehouseRequest);
    WarehouseDTO getWarehouseById(Long id);
    void deleteWarehouseById(Long id);
}
