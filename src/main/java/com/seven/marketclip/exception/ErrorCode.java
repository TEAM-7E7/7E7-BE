package com.seven.marketclip.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /* 400 BAD_REQUEST 잘못된 요청 */
    ALREADY_LOGIN("이미 로그인이 되어있습니다."),
    INVALID_REGISTER_EMAIL("이메일 형식이 유효하지 않습니다."),
    INVALID_REGISTER_PASSWORD( "비밀번호 형식이 유효하지 않습니다."),
    INVALID_REGISTER_USERNAME( "이름을 입력해 주세요."),
    INVALID_LOGIN( "이메일 또는 패스워드를 확인해 주세요."),
    INVALID_BOARD_REQ( "게시글 형식을 확인해 주세요."),
    FAVORITE_ALREADY_EXIST("이미 좋아요가 존재합니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),

    /* 401 UNAUTHORIZED 인증되지 않은 사용자 */
    LOGIN_REQUIRED( "로그인이 필요합니다."),
    NOT_AUTHOR("해당 게시글의 작성자가 아닙니다."),

    /* 404 NOT_FOUND  리소스를 찾을 수 없음 */
    BOARD_NOT_FOUND("해당 게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    FAVORITE_NOT_FOUND("좋아요를 찾을 수 없습니다.");

    private final String message;

    ErrorCode(String message){
        this.message = message;
    }

}
