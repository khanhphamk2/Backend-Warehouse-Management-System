package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.khanhpham.wms.common.UserRole;

import java.io.Serializable;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.User}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends AuditDTO {
    Long id;
    String username;
    String name;
    String email;
    String phone;
    UserRole role;
}