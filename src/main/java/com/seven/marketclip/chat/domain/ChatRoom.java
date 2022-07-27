package com.seven.marketclip.chat.domain;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class ChatRoom implements Serializable, Persistable<String> {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name="GOODS_ID")
    private Goods goods;
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;
    @CreatedDate
    private Date createdDate;
    @Builder
    public ChatRoom(String id, Goods goods, Account account){
        this.id = id;
        this.goods = goods;
        this.account = account;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}