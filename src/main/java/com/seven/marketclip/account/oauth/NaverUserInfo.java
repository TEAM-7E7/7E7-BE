package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.repository.AccountTypeEnum;
import lombok.Getter;

import java.util.Map;

@Getter
public class NaverUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    private final  AccountTypeEnum type=AccountTypeEnum.NAVER;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get("id");
    }
    @Override
    public String getSocial() {
        return "naver";
    }
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public AccountTypeEnum getRole(){return type;}

//    @Override
//    public void idtoEmail(String id){
//        System.out.println("올바르지 않은 접근");
//    };

}
