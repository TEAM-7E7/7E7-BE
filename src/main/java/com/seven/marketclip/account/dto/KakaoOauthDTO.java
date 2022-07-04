package com.seven.marketclip.account.dto;

import com.seven.marketclip.account.AccountTypeEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoOauthDTO {
    private final String id; //이메일로 만들기..
    private final String nickname; //이걸 어디서 받아오지? 이름인데...
    private final AccountTypeEnum type;

    @Builder
    public KakaoOauthDTO(String id, String nickname, AccountTypeEnum type) {
        this.id = id;
        this.nickname = nickname;
        this.type = type;
    }
}
