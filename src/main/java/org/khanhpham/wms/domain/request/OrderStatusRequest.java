package org.khanhpham.wms.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.khanhpham.wms.common.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusRequest {
    private OrderStatus status;
}
