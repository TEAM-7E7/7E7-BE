package com.seven.marketclip.chat.domain;

import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom implements Serializable, Persistable<String> {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name="goods_id")
    private Goods goods;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "chatRoomId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatMessages> messages;

    private LocalDateTime createdDate;
    @Builder
    public ChatRoom(String id, Goods goods, Account account){
        this.id = id;
        this.goods = goods;
        this.account = account;
        this.createdDate = LocalDateTime.now();
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}