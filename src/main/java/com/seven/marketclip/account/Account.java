package com.seven.marketclip.account;

import com.seven.marketclip.Timestamped;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    @Column(unique = true,nullable = false)
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
