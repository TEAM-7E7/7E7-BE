package com.seven.marketclip.account.service;

import com.seven.marketclip.account.AccountTypeEnum;

public interface OAuth2UserInfo {
    String getSocialId(); //해당 소셜 고유 아이디
    String getSocial(); //해당 소셜
    String getEmail(); //이메일
    String getName(); //이름
    AccountTypeEnum getRole();
}
