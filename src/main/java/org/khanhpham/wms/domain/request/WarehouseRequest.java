package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WarehouseRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;
    
    private String description;

    @NotBlank(message = "Manager ID is required")
    private Long managerId;
}
