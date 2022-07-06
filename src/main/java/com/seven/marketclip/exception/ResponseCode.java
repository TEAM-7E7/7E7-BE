package com.seven.marketclip.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ResponseCode {

    /** ( 200 OK / 201 CREATED ) 요청성공 */
    TEST_SUCCESS(OK, "테스트 결과 확인."),

    // 닉네임 확인
    NICKNAME_VALIDATION_SUCCESS(OK, "닉네임을 사용할 수 있습니다."),

    // 이메일 인증
    EMAIL_VALIDATION_SUCCESS(OK, "이메일 인증이 완료되었습니다."),
    EMAIL_DISPATCH_SUCCESS(OK, "해당 이메일로 인증번호를 발송하였습니다."),

    // 회원가입 완료
    SIGNUP_SUCCESS(CREATED, "회원가입이 완료되었습니다."),

    // 게시글
    GOODS_POST_SUCCESS(CREATED, "게시글 작성이 완료되었습니다."),
    GOODS_DELETE_SUCCESS(OK, "게시글 삭제가 완료되었습니다."),
    GOODS_UPDATE_SUCCESS(OK, "게시글 수정이 완료되었습니다."),
    GOODS_BOARD_SUCCESS(OK, "게시글 전체 조회가 완료되었습니다."),
    GOODS_DETAIL_SUCCESS(OK, "게시글 상세페이지를 불러왔습니다."),


    /** 400 BAD_REQUEST 잘못된 요청 */
    // 회원가입 + 로그인
    ALREADY_LOGIN(BAD_REQUEST, "이미 로그인이 되어있습니다."),
    INVALID_REGISTER_EMAIL(BAD_REQUEST, "이메일 형식이 유효하지 않습니다."),
    INVALID_REGISTER_PASSWORD(BAD_REQUEST, "비밀번호 형식이 유효하지 않습니다."),
    INVALID_REGISTER_USERNAME(BAD_REQUEST, "이름을 입력해 주세요."),

    INVALID_LOGIN(BAD_REQUEST, "이메일 또는 패스워드를 확인해 주세요."),


    EMAIL_ALREADY_SENT(BAD_REQUEST, "이미 이메일이 발송되었습니다."),
    EMAIL_ALREADY_EXPIRED(BAD_REQUEST, "이메일 인증시간이 지났습니다, 인증번호를 다시 발급해주세요."),

    UNVERIFIED_EMAIL(BAD_REQUEST, "이메일이 인증되지 않았습니다."),
    INVALID_EMAIL_TOKEN(BAD_REQUEST, "이메일 인증번호가 일치하지 않습니다."),

    INVALID_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다."),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다."),

    // 게시글 작성
    INVALID_GOODS_REQ(BAD_REQUEST, "게시글 형식을 확인해 주세요."),
    WRONG_FILE_TYPE(BAD_REQUEST, "파일의 형식을 확인해 주세요: JPEG, JPG, PNG, BMP, MP4"),
    FILE_UPLOAD_ERROR(BAD_REQUEST, "파일 업로드에 실패했습니다."),

    // validation, integrity 위반
    WRONG_VALIDATION_INTEGRITY(BAD_REQUEST, "validation 또는 무결성 위반."),

    /** 401 UNAUTHORIZED 인증되지 않은 사용자 */
    LOGIN_REQUIRED(UNAUTHORIZED, "로그인이 필요합니다."),
    NOT_AUTHOR(UNAUTHORIZED, "해당 게시글의 작성자가 아닙니다."),

    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다."),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다."),

    /** 403 FORBIDDEN 인증되지 않은 사용자 */
    HEADER_NOT_FOUND(FORBIDDEN, "헤더에 토큰이 존재하지 않습니다."),


    /** 404 NOT_FOUND  리소스를 찾을 수 없음 */
    GOODS_NOT_FOUND(NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
    FAVORITE_NOT_FOUND(NOT_FOUND, "찜한 상품을 찾을 수 없습니다."),
    FILE_NOT_FOUND(NOT_FOUND, "해당 파일을 찾을 수 없습니다."),


    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다."),

    /** 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    USER_ALREADY_EXISTS(CONFLICT, "이미 존재하는 사용자입니다."),
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다."),

    NICKNAME_ALREADY_EXISTS(CONFLICT, "이미 존재하는 닉네임입니다."),

    // 즐겨찾기
    BOOKMARK_ALREADY_EXIST(CONFLICT, "이미 찜 한 상품입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ResponseCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
