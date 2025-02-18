package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private String category;
    private String brand;
    private String color;
    private String size;
    private String price;
    private String quantity;
    private String image;
    private String status;
    private String createdBy;
    private String updatedBy;

}
