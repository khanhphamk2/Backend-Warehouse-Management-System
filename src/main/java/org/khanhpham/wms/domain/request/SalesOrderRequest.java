package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.khanhpham.wms.domain.dto.OrderItemDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
