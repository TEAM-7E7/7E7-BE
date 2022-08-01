package com.seven.marketclip.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class DataResponseCode implements Serializable {
    private final ResponseCode responseCode;
    private final Object data;

    public DataResponseCode(ResponseCode responseCode, Object data){
        this.responseCode = responseCode;
        this.data = data;
    }
}
