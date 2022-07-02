package com.seven.marketclip.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    /* 400 BAD_REQUEST 잘못된 요청 */
    ALREADY_LOGIN(BAD_REQUEST, "이미 로그인이 되어있습니다."),
    INVALID_REGISTER_EMAIL(BAD_REQUEST, "이메일 형식이 유효하지 않습니다."),
    INVALID_REGISTER_PASSWORD(BAD_REQUEST, "비밀번호 형식이 유효하지 않습니다."),
    INVALID_REGISTER_USERNAME(BAD_REQUEST, "이름을 입력해 주세요."),

    INVALID_LOGIN(BAD_REQUEST, "이메일 또는 패스워드를 확인해 주세요."),
    INVALID_BOARD_REQ(BAD_REQUEST, "게시글 형식을 확인해 주세요."),
    FAVORITE_ALREADY_EXIST(BAD_REQUEST, "이미 좋아요가 존재합니다."),
    USER_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 사용자입니다."),

    INVALID_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    CANNOT_FOLLOW_MYSELF(BAD_REQUEST, "자기 자신은 팔로우 할 수 없습니다"),

    /* 401 UNAUTHORIZED 인증되지 않은 사용자 */
    LOGIN_REQUIRED(UNAUTHORIZED, "로그인이 필요합니다."),
    NOT_AUTHOR(UNAUTHORIZED, "해당 게시글의 작성자가 아닙니다."),

    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),

    /* 404 NOT_FOUND  리소스를 찾을 수 없음 */
    BOARD_NOT_FOUND(NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
    FAVORITE_NOT_FOUND(NOT_FOUND, "좋아요를 찾을 수 없습니다."),

    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
