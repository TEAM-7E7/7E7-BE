package com.seven.marketclip.chat.domain;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
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
    @ManyToOne
    @JoinColumn(name="GOODS_ID")
    private Goods goods;
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;

    @CreatedDate // 생성일자임을 나타냅니다. 프론트에서 생성하는 경우 제거
    private LocalDateTime createdAt;
    @Builder
    public ChatRoom(Goods goods, Account account){
        this.goods = goods;
        this.account = account;
    }

}