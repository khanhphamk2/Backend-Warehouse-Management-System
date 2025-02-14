package org.khanhpham.whs.domain.dto;

import lombok.Value;
import org.khanhpham.whs.common.UserRole;

/**
 * DTO for {@link org.khanhpham.whs.domain.model.User}
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