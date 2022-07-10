package com.seven.marketclip.goods.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class StringMultipart {
    private String string;
    private MultipartFile multipartFile;

    public StringMultipart(String string){
        this.string = string;
    }

    public StringMultipart(MultipartFile multipartFile){
        this.multipartFile = multipartFile;
    }

}
