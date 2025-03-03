package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @NotEmpty(message = "Username is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @NotEmpty(message = "Password is required")
    private String password;
}
