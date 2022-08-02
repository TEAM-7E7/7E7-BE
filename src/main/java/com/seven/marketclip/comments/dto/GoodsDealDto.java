package com.seven.marketclip.comments.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsDealDto {
    private String chatRoomId;

    //게시글 아이디
    private Long goodsId;

    private Long sellerId;

    //상대 아이디
    private Long buyerId;

}
