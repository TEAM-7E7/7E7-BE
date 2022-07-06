package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.AccountTypeEnum;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    private final  AccountTypeEnum type=AccountTypeEnum.KAKAO;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void printAttribute(){
        attributes.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
    }

    @Override
    public String getSocialId() {
        return String.valueOf(attributes.get("id"));
    }
    @Override
    public String getSocial() {
        return "kakao";
    }
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        LinkedHashMap<String,String> sd = (LinkedHashMap)attributes.get("kakao_account");
        return  sd.get("email");
    }

    @Override
    public AccountTypeEnum getRole(){return type;}

}
