package org.khanhpham.wms.domain.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.khanhpham.wms.domain.dto.OrderItemDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesOrderRequest {
    private Long customerId;
    private LocalDate orderDate;
    private LocalDate expectedShipmentDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private double totalAmount;
    private String notes;
    private List<OrderItemDTO> products;
}
