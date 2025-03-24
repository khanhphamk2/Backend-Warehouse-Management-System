package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.entity.SalesOrder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link SalesOrder}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesOrderDTO extends AuditDTO implements Serializable {
    private Long id;
    private Long customerId;
    private LocalDate orderDate;
    private LocalDate expectedShipmentDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    OrderStatus status;
    List<ShortProductDTO> products;
}