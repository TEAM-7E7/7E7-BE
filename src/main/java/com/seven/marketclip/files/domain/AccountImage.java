package com.seven.marketclip.files.domain;

import com.seven.marketclip.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class AccountImage {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public AccountImage(Account account, String imageUrl){
        this.account = account;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public void updateUrl(String url){
        this.imageUrl = url;
    }

}
