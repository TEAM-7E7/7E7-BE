package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.AccountTypeEnum;
import lombok.Getter;

import java.util.Map;

@Getter
public class GoogleUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    private final AccountTypeEnum type = AccountTypeEnum.GOOGLE;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void printAttribute(){
        attributes.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get("sub");
    }
    @Override
    public String getSocial() {
        return "google";
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
