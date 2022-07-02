package com.seven.marketclip.account;

import com.seven.marketclip.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@NoArgsConstructor
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

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountRoleEnum role;

    @Column(name = "rating_score")
    private Double ratingScore;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountTypeEnum type;

    @Column(name = "refresh_token")
    private String refreshToken;


    @Builder
    public Account(String nickname, String email, String password, AccountRoleEnum role, AccountTypeEnum type ) {
        this.nickname =nickname;
        this.email=email;
        this.password=password;
        this.role=role;
        this.type=type;
    }

    //계정 타입 (일반)
    public void saveAccountType(AccountTypeEnum accountTypeEnum){
        this.type = accountTypeEnum;
    }

    //패스워드 인코드
    public void EncodePassword(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.password = bCryptPasswordEncoder.encode(password);
    }

    //리프레쉬 토큰 변경
    public void refreshTokenChange(String refreshToken){
        this.refreshToken = refreshToken;
    }

}
