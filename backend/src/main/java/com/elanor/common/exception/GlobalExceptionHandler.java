package com.elanor.common.exception;

import com.elanor.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("Business exception occurred [traceId: {}]: {}", traceId, ex.getMessage());

        ErrorCode code = ex.getErrorCode();
        ApiErrorResponse response = ApiErrorResponse.of(
                code.name(),
                ex.getMessage(),
                ex.getDetails(),
                traceId
        );
        return new ResponseEntity<>(response, code.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiErrorResponse response = ApiErrorResponse.of(
                ErrorCode.VALIDATION_ERROR.name(),
                "Validation failed for one or more fields.",
                errors,
                traceId
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        ApiErrorResponse response = ApiErrorResponse.of(
                ErrorCode.AUTH_UNAUTHORIZED.name(),
                ex.getMessage() != null ? ex.getMessage() : "Unauthorized",
                null,
                traceId
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        ApiErrorResponse response = ApiErrorResponse.of(
                ErrorCode.FORBIDDEN.name(),
                "You do not have permission to access this resource.",
                null,
                traceId
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Unhandled internal server exception [traceId: {}]", traceId, ex);

        ApiErrorResponse response = ApiErrorResponse.of(
                ErrorCode.INTERNAL_ERROR.name(),
                "An unexpected internal error occurred. Please try again later.",
                null,
                traceId
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
