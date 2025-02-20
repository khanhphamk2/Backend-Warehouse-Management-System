package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String contactInfo;
    private String description;
}
