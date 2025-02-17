package org.khanhpham.wms.domain.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;
import java.util.Set;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.Customer}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO extends AuditDTO {
    Long id;
    String name;
    String phone;
    @Email
    String email;
    String address;
    Set<Long> salesOrderIds;
}