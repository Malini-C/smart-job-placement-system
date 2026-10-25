package com.placementsystem.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ═══════════════════════════════════════════════════════════
    // 1. NOT FOUND (404)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // ═══════════════════════════════════════════════════════════
    // 2. BAD REQUEST (400) - Business logic errors
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    // ═══════════════════════════════════════════════════════════
    // 3. VALIDATION ERRORS (400) - Field-level validation
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fe) {
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        log.warn("Validation failed: {}", details);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Invalid input data",
                details,
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ═══════════════════════════════════════════════════════════
    // 4. ENTITY-LEVEL VALIDATION (400)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        log.warn("Constraint violation: {}", details);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Database constraint violation",
                details,
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ═══════════════════════════════════════════════════════════
    // 5. MALFORMED JSON (400)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Malformed JSON: {}", ex.getMessage());

        String message = "Request body is malformed or contains invalid data";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String cause = ex.getCause().getMessage();
            if (cause.contains("LocalDate")) {
                message = "Invalid date format. Use YYYY-MM-DD (e.g., 2026-12-31)";
            } else if (cause.contains("LocalTime")) {
                message = "Invalid time format. Use HH:MM:SS (e.g., 10:30:00)";
            } else if (cause.contains("ApplicationStatus")) {
                message = "Invalid status. Allowed: APPLIED, SHORTLISTED, INTERVIEW_SCHEDULED, SELECTED, REJECTED";
            }
        }

        return build(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    // ═══════════════════════════════════════════════════════════
    // 6. MISSING PARAMETERS (400)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                "Required parameter missing: " + ex.getParameterName(), request);
    }

    // ═══════════════════════════════════════════════════════════
    // 7. TYPE MISMATCH (400)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = "Invalid value for '" + ex.getName() + "': " + ex.getValue();
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    // ═══════════════════════════════════════════════════════════
    // 8. WRONG HTTP METHOD (405)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                "HTTP method " + ex.getMethod() + " is not supported for this endpoint", request);
    }

    // ═══════════════════════════════════════════════════════════
    // 9. DATABASE INTEGRITY (409)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage());

        String message = "Database constraint violation";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Duplicate entry")) {
                message = "A record with these details already exists";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Cannot delete because it is referenced by other records";
            }
        }

        return build(HttpStatus.CONFLICT, "Conflict", message, request);
    }

    // ═══════════════════════════════════════════════════════════
    // 10. AUTHENTICATION ERRORS (401)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {
        log.warn("Bad credentials: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password", request);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Account is disabled", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            AuthenticationException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication failed", request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
    }

    // ═══════════════════════════════════════════════════════════
    // 11. AUTHORIZATION ERRORS (403)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "Forbidden",
                "You do not have permission to access this resource", request);
    }

    // ═══════════════════════════════════════════════════════════
    // 12. STATIC RESOURCE NOT FOUND (404) - Silent
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(
            NoResourceFoundException ex, WebRequest request) {
        // Don't log these — they're just missing favicon.ico or similar
        return build(HttpStatus.NOT_FOUND, "Not Found",
                "Resource not found: " + ex.getResourcePath(), request);
    }

    // ═══════════════════════════════════════════════════════════
    // 13. NO HANDLER FOUND (404)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(
            NoHandlerFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, "Not Found",
                "No endpoint found for " + ex.getHttpMethod() + " " + ex.getRequestURL(), request);
    }

    // ═══════════════════════════════════════════════════════════
    // 14. FALLBACK - Unexpected errors (500)
    // ═══════════════════════════════════════════════════════════
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        log.error("═══════════════════════════════════════");
        log.error("UNEXPECTED ERROR");
        log.error("Message: {}", ex.getMessage());
        log.error("Class: {}", ex.getClass().getName());
        log.error("Stack:", ex);
        log.error("═══════════════════════════════════════");

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.", request);
    }

    // ═══════════════════════════════════════════════════════════
    // Helper to build ErrorResponse
    // ═══════════════════════════════════════════════════════════
    private ResponseEntity<ErrorResponse> build(
            HttpStatus status, String error, String message, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                error,
                message,
                request.getDescription(false)
        );
        return ResponseEntity.status(status).body(response);
    }
}