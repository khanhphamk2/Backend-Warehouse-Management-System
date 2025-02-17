package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.SalesOrderItem}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesOrderItemDTO extends AuditDTO {
    Long id;
    Long salesOrderId;
    Long productId;
    long quantity;
    double unitPrice;
}