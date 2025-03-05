package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequest {
    private Long supplierId;
    private Instant orderDate;
    private Instant receiveDate;
    private BigDecimal totalAmount;
}
