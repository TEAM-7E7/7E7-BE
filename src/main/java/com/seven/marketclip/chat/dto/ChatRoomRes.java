package com.seven.marketclip.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomRes {
    private Long goodsId;
    private String partner;     //거래 상대
    private String partnerProfileUrl; // 상대 프로필 사진 주소
    private String lastMessage; // 마지막 대화내용
    private String goodsFileUrl; // 상품의 사진 주소
    private Date lastDate;      // 마지막 전송 일자
    private Integer readOrNotCnt; //안읽은 알림 개수
}
