package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.khanhpham.wms.common.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.PurchaseOrder}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrderDTO extends AuditDTO implements Serializable {
    Long id;
    Long supplierId;
    LocalDate orderDate;
    LocalDate receiveDate;
    BigDecimal totalAmount;
    OrderStatus status;
    List<ShortProductDTO> products;
}