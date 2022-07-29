/*
package com.seven.marketclip.block.domain;

import com.seven.marketclip.account.domain.Account;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockAccounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account userId;

    @ManyToOne
    private Account targetId;

    private LocalDateTime createdAt;

    @Builder
    public BlockAccounts(Long id, Account userId, Account targetId, LocalDateTime createdAt){
        this.id = id;
        this.userId = userId;
        this.targetId = targetId;
        this.createdAt = createdAt;
    }
}
*/
