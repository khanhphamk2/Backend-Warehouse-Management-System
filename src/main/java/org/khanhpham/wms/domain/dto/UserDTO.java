package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.khanhpham.wms.domain.response.RoleResponse;

import java.util.Set;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.User}
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
    boolean isActive;
    Set<RoleResponse> roles;
}