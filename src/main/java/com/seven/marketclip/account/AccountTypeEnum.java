package com.seven.marketclip.account;

public enum AccountTypeEnum {
    MARKETCLIP(AccountType.MARKETCLIP),
    KAKAO(AccountType.KAKAO),
    NAVER(AccountType.NAVER),
    GOOGLE(AccountType.GOOGLE);

    private final String type;

    AccountTypeEnum(String type) {
        this.type = type;
    }

    public static class AccountType{
        public static final String MARKETCLIP = "TYPE_MARKETCLIP";
        public static final String KAKAO = "TYPE_KAKAO";
        public static final String NAVER = "TYPE_NAVER";
        public static final String GOOGLE = "TYPE_GOOGLE";
    }

}
