package org.khanhpham.wms.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.Category}
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO extends AuditDTO {
    Long id;
    @NotBlank(message = "Name is mandatory")
    String name;
}