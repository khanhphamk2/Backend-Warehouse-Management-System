package org.khanhpham.wms.domain.dto;

import lombok.*;
import org.khanhpham.wms.domain.entity.AuditEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link AuditEntity}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public abstract class AuditDTO {
    LocalDateTime createdDate;
    String createdBy;
    LocalDateTime lastModifiedDate;
    String lastModifiedBy;
}