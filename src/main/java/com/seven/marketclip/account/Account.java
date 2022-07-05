package com.seven.marketclip.account;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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
    private Double ratingScore = 0.0;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountTypeEnum type;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToMany(mappedBy = "account")
    private List<Goods> goodsList;

    @Builder
    public Account(String nickname, String email, String password, AccountRoleEnum role, AccountTypeEnum type ) {
        this.nickname =nickname;
        this.email=email;
        this.password=password;
        this.role=role;
        this.type=type;
    }

    public Account(AccountReqDTO accountReqDTO){
        this.nickname = accountReqDTO.getNickname();
        this.email = accountReqDTO.getEmail();
        this.password = accountReqDTO.getPassword();
    }

    //계정 타입 (일반)
    public void saveAccountType(AccountTypeEnum accountTypeEnum){
        this.type = accountTypeEnum;
    }

    //패스워드 인코드
    public void encodePassword(String encodedPassword){
        this.password = encodedPassword;
    }

    //리프레쉬 토큰 변경
    public void changeRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    //소셜에서 카카오만 이메일 값을 아이디 값으로 대체
    public void changeIdtoEmail(String id){this.email = id;}

}
