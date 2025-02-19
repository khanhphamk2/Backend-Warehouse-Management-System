package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
@Getter
public class CustomerRequest {
    @NotNull
    @Email
    private String email;
    private String name;
    private String phone;
    private String address;
}
