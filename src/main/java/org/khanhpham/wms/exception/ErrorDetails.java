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
    private final String title;
    private final int status;
    private final Object message;
    private final String details;
    private final Instant timestamp;
}
