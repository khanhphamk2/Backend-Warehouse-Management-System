package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.khanhpham.wms.domain.entity.AuditEntity;

import java.time.Instant;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdDate;
    String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime lastModifiedDate;
    String lastModifiedBy;
}