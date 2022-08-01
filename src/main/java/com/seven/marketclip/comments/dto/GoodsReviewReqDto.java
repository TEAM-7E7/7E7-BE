package com.seven.marketclip.comments.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsReviewReqDto {

    private String message; //후기 메시지

    private Double kindness; //친절

    private Double responseSpeed; //응답속도

    private Double quality; //풂질

    private Double appointment; //시간약속

    @Builder
    public GoodsReviewReqDto(String message, Double kindness, Double responseSpeed, Double quality, Double appointment) {
        this.message = message;
        this.kindness = kindness;
        this.responseSpeed = responseSpeed;
        this.quality = quality;
        this.appointment = appointment;
    }
}
