package org.khanhpham.wms.domain.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.khanhpham.wms.common.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusRequest {
    private OrderStatus status;
}
