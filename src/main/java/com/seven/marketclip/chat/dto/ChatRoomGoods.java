package com.seven.marketclip.chat.dto;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomGoods {
    //왼쪽 채팅방에 표시될 내용
    //상품정보
    private Long goodsId;           //상품 정보를 클라이언트에 주고 받기 위함
    private String goodsFileUrl;    //상품의 이미지 (대표 사진은 두번째 Url)

    //채팅방 정보
    private String chatRoomId;
    //채팅방의 메시지
    private String lastMessage;     //마지막 대화 내용
    private Date lastDate;          //마지막 대화 일자
    private Long checkReadCnt;   //읽지 않은 알림 개수

    //대화 상대
    private Long partnerId;
    private String partner;             //대화 상대 닉네임
    private String partnerProfileUrl;   //대화 상대 프로필 사진

    @Builder
    public ChatRoomGoods(ChatRoom chatRoom, Long loginId, Long checkReadCnt){
        Goods goods = chatRoom.getGoods();
        Account buyer = chatRoom.getAccount();
        List<ChatMessages> messages = chatRoom.getMessages();
        this.chatRoomId = chatRoom.getId();
        this.goodsId = goods.getId();
        this.goodsFileUrl = goods.getGoodsImages().get(0).getImageUrl(); // 사진 없을때 예외처리

        if(messages.isEmpty() || messages.size() == 1){
            this.lastMessage = "";
            this.lastDate = null;
        }else{
            this.lastMessage = messages.get(messages.size()-1).getMessage();
            this.lastDate = messages.get(messages.size()-1).getCreatedAt();
        }
        this.checkReadCnt = checkReadCnt;
        if(buyer.getId() != loginId){                 //대화 상대 구분
            this.partnerId = buyer.getId();
            this.partner = buyer.getNickname();
            this.partnerProfileUrl = buyer.getProfileImgUrl().getImageUrl();
        }else{
            this.partnerId = goods.getAccount().getId();
            this.partner = goods.getAccount().getNickname();
            this.partnerProfileUrl = goods.getAccount().getProfileImgUrl().getImageUrl();
        }

    }
}
