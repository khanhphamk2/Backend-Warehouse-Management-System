package org.khanhpham.wms.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
public class ErrorDetails {
    private final Instant timestamp;
    private final String message;
    private final String details;
    private final String cause;
}
