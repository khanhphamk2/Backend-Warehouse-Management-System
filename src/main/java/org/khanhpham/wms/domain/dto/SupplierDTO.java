package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.Supplier}
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierDTO extends AuditDTO {
    Long id;
    String name;
    String contactInfo;
    String address;
    String phone;
    String email;
    String description;
    List<Long> purchaseOrderIds;
}