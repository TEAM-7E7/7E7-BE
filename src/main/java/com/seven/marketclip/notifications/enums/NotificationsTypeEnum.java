package com.seven.marketclip.notifications.enums;

public enum NotificationsTypeEnum {
    CHAT(NotificationsType.CHAT),
    REVIEW(NotificationsType.REVIEW),
    WISHLIST(NotificationsType.WISHLIST);

    private final String type;

    NotificationsTypeEnum(String type) { this.type = type; }

    public static class NotificationsType{
        public static final String CHAT = "TYPE_CHAT";
        public static final String REVIEW = "TYPE_REVIEW";
        public static final String WISHLIST = "TYPE_WISHLIST";
    }

}
