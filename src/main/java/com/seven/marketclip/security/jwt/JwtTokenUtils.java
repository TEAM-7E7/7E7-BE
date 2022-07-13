package com.seven.marketclip.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.seven.marketclip.security.UserDetailsImpl;

import java.util.Date;

public final class JwtTokenUtils {


    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    // JWT 토큰의 유효기간: 3일 (단위: seconds)
    private static final int JWT_TOKEN_VALID_SEC = MINUTE*1;
    private static final int REFRESH_TOKEN_VALID_SEC = MINUTE*3;
    // JWT 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000;
    private static final int REFRESH_TOKEN_VALID_MILLI_SEC = REFRESH_TOKEN_VALID_SEC * 1000;

    public static final String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";

    public static final String CLAIM_USER_ID = "USER_ID";
    public static final String CLAIM_USER_NAME = "USER_NAME";
    public static final String CLAIM_USER_EMAIL = "USER_EAMIL";
    public static final String CLAIM_USER_PROFILEIMG = "USER_PROFILEIMG";
    public static final String CLAIM_USER_ROLE = "USER_ROLE";

    public static final String JWT_SECRET = "jwt_secret_!@#$%";

    public static String generateRefreshToken(UserDetailsImpl userDetails) {
        System.out.println("로그인 필터 8-리프레쉬");
        String token = null;

        try {
            token = JWT.create()
                    .withIssuer("sparta")
                    .withClaim(CLAIM_USER_ID, userDetails.getId())
                    // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }
    public static String generateJwtToken(UserDetailsImpl userDetails) {
        System.out.println("로그인 필터 8");
        String token = null;

        try {
            token = JWT.create()
                    .withIssuer("sparta")
                    .withClaim(CLAIM_USER_ID, userDetails.getId())
                    .withClaim(CLAIM_USER_NAME, userDetails.getNickname())
                    .withClaim(CLAIM_USER_EMAIL, userDetails.getUsername())
                    .withClaim(CLAIM_USER_PROFILEIMG, userDetails.getProfileImgUrl())
                    .withClaim(CLAIM_USER_ROLE, String.valueOf(userDetails.getRole()))
                    // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
}