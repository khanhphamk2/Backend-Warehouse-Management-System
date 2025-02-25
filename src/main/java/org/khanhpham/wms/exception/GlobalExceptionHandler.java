package org.khanhpham.wms.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                        WebRequest webRequest) {
        ErrorDetails errorDetails = createErrorDetails(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                webRequest
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDetails> handleCustomException(CustomException exception,
                                                              WebRequest webRequest) {
        ErrorDetails errorDetails = createErrorDetails(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                webRequest
        );
        return new ResponseEntity<>(errorDetails, exception.getStatus());
    }

    // global exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception,
                                                              WebRequest webRequest) {
        ErrorDetails errorDetails = createErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                webRequest
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

//        String errorMessages = String.join(", ", errors.values());

        ErrorDetails errorDetails = createErrorDetails(
                HttpStatus.BAD_REQUEST,
                errors,
                request
        );

       return ResponseEntity.badRequest().body(errorDetails);
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException exception,
//                                                                    WebRequest webRequest) {
//        ErrorDetails errorDetails = createErrorDetails(exception, webRequest);
//        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
//    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorDetails> handleConflictException(ResourceAlreadyExistException exception,
                                                                WebRequest webRequest) {
        ErrorDetails errorDetails = createErrorDetails(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                webRequest
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    private ErrorDetails createErrorDetails(HttpStatus http, Object exception, WebRequest webRequest) {
        return new ErrorDetails(
                http.getReasonPhrase(),
                http.value(),
                exception,
                webRequest.getDescription(false),
                Instant.now());
    }
}
