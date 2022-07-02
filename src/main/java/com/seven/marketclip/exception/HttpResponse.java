package com.seven.marketclip.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class HttpResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String response;
    private final String code;
    private final String message;

    @Builder
    public HttpResponse(int status, String response, String code, String message) {
        this.status = status;
        this.response = response;
        this.code = code;
        this.message = message;
    }

    public static ResponseEntity<HttpResponse> toResponseEntity(ResponseCode responseCode) {
        return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(HttpResponse.builder()
                        .status(responseCode.getHttpStatus().value())
                        .response(responseCode.getHttpStatus().name())
                        .code(responseCode.name())
                        .message(responseCode.getMessage())
                        .build()
                );
    }
}