package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.khanhpham.wms.domain.dto.OrderItemDTO;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequest {
    private Long supplierId;
    private LocalDate orderDate;
    private LocalDate receiveDate;
    private List<OrderItemDTO> products;
    private String notes;

}
