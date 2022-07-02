package com.seven.marketclip.account.dto;

import com.seven.marketclip.account.AccountTypeEnum;
import lombok.Getter;

@Getter
public class KakaoOauthDto {
    private String id; //이메일로 만들기..
    private String nickname; //이걸 어디서 받아오지? 이름인데...

    private AccountTypeEnum type;
    public KakaoOauthDto(String id, String nickname,AccountTypeEnum type) {
        this.id = id;
        this.nickname = nickname;
        this.type = type;
    }
}
