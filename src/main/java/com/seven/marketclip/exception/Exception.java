package com.seven.marketclip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/*@RestControllerAdvice
public class Exception extends RuntimeException {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("IllegalArgumentException", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(S3UploadFailedException.class)
    public ResponseEntity<Map<String, String>> handleS3UploadFailedException(S3UploadFailedException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("IdDuplicateException", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IdDuplicateException.class)
    public ResponseEntity<Map<String, String>> handleIdDuplicateException(IdDuplicateException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("IdDuplicateException", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }

}*/

