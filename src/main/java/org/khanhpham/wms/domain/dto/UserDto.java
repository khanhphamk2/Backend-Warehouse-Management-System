package org.khanhpham.wms.domain.dto;

import lombok.Value;
import org.khanhpham.wms.common.UserRole;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.User}
 */
@Value
public class UserDto {
    Long id;
    String username;
    String password;
    String name;
    String email;
    String phone;
    UserRole role;
}