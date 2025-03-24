package org.khanhpham.wms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.Notification}
 */
@AllArgsConstructor
@Getter
@Data
@Builder
public class NotificationDTO implements Serializable {
    private final Long id;
    private final String title;
    private final Instant timestamp;
    private final String message;
}