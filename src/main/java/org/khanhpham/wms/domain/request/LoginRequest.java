package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotBlank(message = "Username or Email is required")
    @NotEmpty(message = "Username or Email is required")
    String identity;

    @NotBlank(message = "Password is required")
    @NotEmpty(message = "Password is required")
    String password;
}
