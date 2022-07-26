package com.seven.marketclip.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RoomMake {
    String id;
    Long goodsId;
    Long accountId;
    Date createdAt;
}
