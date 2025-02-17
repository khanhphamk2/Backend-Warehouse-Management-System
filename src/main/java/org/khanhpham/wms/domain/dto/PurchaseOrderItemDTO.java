package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.PurchaseOrderItem}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrderItemDTO extends AuditDTO {
    Long id;
    Long purchaseOrderId;
    Long productId;
    long quantity;
    double unitPrice;
}