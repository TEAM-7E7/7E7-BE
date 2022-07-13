package com.seven.marketclip.account;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.wishList.domain.WishLists;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    private String profileImgUrl;

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

    @OneToMany(mappedBy = "account", orphanRemoval = true)
    private List<WishLists> wishLists;

    @Builder
    public Account(Long id, String nickname, String email, String password, AccountRoleEnum role, AccountTypeEnum type, String profileImgUrl) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
        this.role = role;
        this.type = type;
        this.profileImgUrl = profileImgUrl;
    }

    public Account(AccountReqDTO accountReqDTO) {
        this.nickname = accountReqDTO.getNickname();
        this.email = accountReqDTO.getEmail();
        this.password = accountReqDTO.getPassword();
    }

    public Account(UserDetailsImpl userDetails) {
        this.id = userDetails.getId();
        this.email = userDetails.getUsername();
    }

    //계정 타입 (일반)
    public void saveAccountType(AccountTypeEnum accountTypeEnum) {
        this.type = accountTypeEnum;
    }

    //패스워드 인코드
    public void encodePassword(BCryptPasswordEncoder byCryptPasswordEncoder){
        this.password = byCryptPasswordEncoder.encode(password);
    }

    //리프레쉬 토큰 변경
    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //소셜에서 카카오만 이메일 값을 아이디 값으로 대체
    public void changeIdtoEmail(String id) {
        this.email = id;
    }

    //프로필 이미지 변경
    public void changeProfileImg(String file){this.profileImgUrl = file;}
    public void changeNickname(String nickname){this.nickname = nickname;}
    public void changePassword(String password){this.password = password;}

}
