package org.khanhpham.whs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class ErrorDetails {
    private final Instant timestamp;
    private final String message;
    private final String details;
}
