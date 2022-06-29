package com.seven.marketclip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*@RestControllerAdvice
public class HttpErrorResponse {

    @ExceptionHandler(InvalidException.class)
    ResponseEntity<ErrorRes> handleInvalidException(InvalidException e) {
        ErrorRes errorRes = ErrorRes.builder().status(400).message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    ResponseEntity<ErrorRes> handleUnAuthorizedException(UnAuthorizedException e) {
        ErrorRes errorRes = ErrorRes.builder().status(401).message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorRes> handleNotFoundException(NotFoundException e) {
        ErrorRes errorRes = ErrorRes.builder().status(404).message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRes);
    }
}*/
