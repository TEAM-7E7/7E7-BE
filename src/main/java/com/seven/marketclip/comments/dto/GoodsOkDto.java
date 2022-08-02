package com.seven.marketclip.comments.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsOkDto {
    private String chatRoomId;
    //게시글 아이디
    private Long goodsId;

    private Long sellerId;
    //상대 아이디
    private Long buyerId;

    //거래후기 테이블 아이디
    private Long reviewId;

    private String message; //후기 메시지

    private Double kindness; //친절

    private Double responseSpeed; //응답속도

    private Double quality; //풂질

    private Double appointment; //시간약속

    private boolean status;

}
