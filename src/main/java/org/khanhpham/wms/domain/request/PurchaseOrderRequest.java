package org.khanhpham.wms.domain.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.khanhpham.wms.domain.dto.OrderItemDTO;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderRequest {
    private Long supplierId;
    private LocalDate orderDate;
    private LocalDate receiveDate;
    private List<OrderItemDTO> products;
    private String notes;

}
