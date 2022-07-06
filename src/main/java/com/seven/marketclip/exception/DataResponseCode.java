package com.seven.marketclip.exception;

import lombok.Getter;

@Getter
public class DataResponseCode {
    private final ResponseCode responseCode;
    private final Object data;

    public DataResponseCode(ResponseCode responseCode, Object data){
        this.responseCode = responseCode;
        this.data = data;
    }
}
