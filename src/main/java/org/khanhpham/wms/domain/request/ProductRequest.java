package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Categories is required")
    private Set<Long> categoryIds;

    @NotBlank(message = "Supplier is required")
    private Long supplierId;

    @NotBlank(message = "Warehouse is required")
    private Long warehouseId;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Price is required")
    private BigDecimal price;

    @NotBlank(message = "Quantity is required")
    private LocalDateTime expiryDate;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private List<Long> inventoryItemsId;
}
