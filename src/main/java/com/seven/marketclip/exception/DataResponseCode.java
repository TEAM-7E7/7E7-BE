package com.seven.marketclip.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DataResponseCode implements Serializable {
    private ResponseCode responseCode;
    private Object data;

    @Builder
    public DataResponseCode(ResponseCode responseCode, Object data){
        this.responseCode = responseCode;
        this.data = data;
    }

}
