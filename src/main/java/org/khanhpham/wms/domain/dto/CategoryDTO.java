package org.khanhpham.wms.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.Category}
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO extends AuditDTO implements Serializable {
    Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 255, message = "Name must be at most 255 characters")
    String name;

    String description;
}