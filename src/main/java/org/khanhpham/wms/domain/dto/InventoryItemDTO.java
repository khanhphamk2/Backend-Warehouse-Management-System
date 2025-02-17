package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.InventoryItem}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItemDTO extends AuditDTO {
    Long id;
    Long warehouseId;
    Long productId;
    Integer quantity;
}