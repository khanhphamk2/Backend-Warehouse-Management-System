package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.khanhpham.wms.domain.model.SalesOrder;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link SalesOrder}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesOrderDTO extends AuditDTO {
    LocalDateTime updatedDate;
    Long id;
    Long customerId;
    Instant orderDate;
    String status;
    List<Long> orderItemIds;
}