package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.khanhpham.wms.domain.dto.ProductPurchaseDTO;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequest {
    private Long supplierId;
    private Instant orderDate;
    private Instant receiveDate;
    private List<ProductPurchaseDTO> products;
}
