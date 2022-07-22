package com.seven.marketclip.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class HttpResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final Integer status;
    private final String response;
    private final String code;
    private final String message;
    private Object data;

    @Builder
    public HttpResponse(Integer status, String response, String code, String message, Object data) {
        this.status = status;
        this.response = response;
        this.code = code;
        this.message = message;
        this.data = data;
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

    public static ResponseEntity<HttpResponse> toResponseEntity(DataResponseCode dataResponseCode) {
        ResponseCode responseCode = dataResponseCode.getResponseCode();
        Object data = dataResponseCode.getData();
        return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(HttpResponse.builder()
                        .status(responseCode.getHttpStatus().value())
                        .response(responseCode.getHttpStatus().name())
                        .code(responseCode.name())
                        .message(responseCode.getMessage())
                        .data(data)
                        .build()
                );
    }
}