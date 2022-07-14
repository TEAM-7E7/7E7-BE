package com.seven.marketclip.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class MyChatRooms {
    private Long goodsId;
    private Long noRead;
    private String profileImg;
    private String goodsImg;
    private String message;
    private Date messageLastDate;
}
