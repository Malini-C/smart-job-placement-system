package com.placementsystem.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private List<String> details;
    private String path;
    private LocalDateTime timestamp;

    // Constructor for simple errors
    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with details
    public ErrorResponse(int status, String error, String message, List<String> details, String path) {
        this(status, error, message, path);
        this.details = details;
    }
}