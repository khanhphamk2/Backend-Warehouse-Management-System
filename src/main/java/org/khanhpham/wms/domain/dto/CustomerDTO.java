package org.khanhpham.wms.domain.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.Customer}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO extends AuditDTO implements Serializable {
    Long id;
    String name;
    String phone;
    @Email
    String email;
    String address;
    Set<Long> salesOrderIds;
}