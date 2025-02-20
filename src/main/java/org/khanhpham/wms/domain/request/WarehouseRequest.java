package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WarehouseRequest {
    private String name;
    private String address;
    private String location;
    private String warehouseCode;
    private String description;
    private Long managerId;
}
