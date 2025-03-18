package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Categories is required")
    private Set<Long> categoryIds;

    @NotBlank(message = "Supplier is required")
    private Long supplierId;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Price is required")
    private BigDecimal price;

    @NotBlank(message = "Quantity is required")
    private Integer quantity;

    @NotBlank(message = "Quantity is required")
    private LocalDateTime expiryDate;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
