package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.Product}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO extends AuditDTO implements Serializable {
    Long id;
    String name;
    String description;
    @NotNull
    BigDecimal price;
    @NotBlank(message = "SKU is required")
    String sku;
    LocalDateTime expiryDate;
    String unit;
    @NotBlank(message = "Image URL is required")
    String imageUrl;
    int quantity;
    boolean isActive;
    Set<CategoryDTO> categories;
    SupplierDTO supplier;
}