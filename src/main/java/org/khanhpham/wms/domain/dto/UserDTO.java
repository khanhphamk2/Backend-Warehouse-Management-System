package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.khanhpham.wms.domain.response.RoleResponse;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.User}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends AuditDTO implements Serializable {
    Long id;
    String username;
    String name;
    String email;
    String phone;
    boolean isActive;
    Set<RoleResponse> roles;
}