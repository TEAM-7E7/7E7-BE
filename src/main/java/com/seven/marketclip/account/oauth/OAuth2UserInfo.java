package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.repository.AccountTypeEnum;

public interface OAuth2UserInfo {

//    void idtoEmail(String id);
    String getSocialId(); //해당 소셜 고유 아이디
    String getSocial(); //해당 소셜
    String getEmail(); //이메일
    String getName(); //이름
    AccountTypeEnum getRole();
}
