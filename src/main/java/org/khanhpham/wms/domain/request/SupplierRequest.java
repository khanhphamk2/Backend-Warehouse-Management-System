package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(regexp = "^\\+(?:\\d ?){6,14}\\d$", message = "Invalid phone number")
    @NotBlank(message = "Phone number is required")
    private String phone;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    private String contactInfo;

    private String description;
}
