package com.seven.marketclip.chat.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long goodsId;
    private Long buyerId;
    @CreatedDate // 생성일자임을 나타냅니다. 프론트에서 생성하는 경우 제거
    private LocalDateTime createdAt;
    @Builder
    public ChatRoom(Long goodsId, Long buyerId){
        this.goodsId = goodsId;
        this.buyerId = buyerId;
    }

}