package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.Product}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO extends AuditDTO {
    Long id;
    String name;
    String description;
    @NotNull
    BigDecimal price;
    @NotBlank(message = "SKU is required")
    String sku;
    LocalDateTime expiryDate;
    String unit;
    String imageUrl;
    Set<CategoryDTO> categories;
    WarehouseDTO warehouse;
    SupplierDTO supplier;
    InventoryItemDTO inventoryItem;
}