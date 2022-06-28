package com.seven.marketclip.account;

import com.seven.marketclip.Timestamped;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Account extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountRoleEnum role;

    @Column(name = "rating_score")
    private Double ratingScore;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountTypeEnum type;

}
