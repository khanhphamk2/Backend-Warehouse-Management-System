package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.PurchaseOrder}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrderDTO extends AuditDTO {
    Long id;
    Long supplierId;
    Instant orderDate;
    Instant receiveDate;
    BigDecimal totalAmount;
    String status;
    List<ShortProductDTO> products;
}