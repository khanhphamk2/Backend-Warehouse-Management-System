package org.khanhpham.whs.domain.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link org.khanhpham.whs.domain.model.AudiEntity}
 */
@Value
public class AudiDto {
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
}