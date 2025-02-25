package org.khanhpham.wms.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    @NotEmpty(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;
}
