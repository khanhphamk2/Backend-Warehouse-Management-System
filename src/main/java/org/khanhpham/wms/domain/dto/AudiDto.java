package org.khanhpham.wms.domain.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link org.khanhpham.wms.domain.model.AudiEntity}
 */
@Value
public class AudiDto {
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
}