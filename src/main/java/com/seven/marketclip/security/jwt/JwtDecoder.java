package com.seven.marketclip.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.seven.marketclip.account.AccountRoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

import static com.seven.marketclip.security.jwt.JwtTokenUtils.*;


@Component
public class JwtDecoder {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Long decodeUserId(String token) {
        DecodedJWT decodedJWT = isValidToken(token).orElseThrow(
                () -> new IllegalArgumentException("유효한 토큰이 아닙니다.1")
        );

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.2");
        }

        Long userid = decodedJWT
                .getClaim(CLAIM_USER_ID)
                .asLong();
//        System.out.println("Long 파서:" + userid);

        return userid;
    }

    public String decodeImg(String token) {
        DecodedJWT decodedJWT = isValidToken(token).orElseThrow(
                () -> new IllegalArgumentException("유효한 토큰이 아닙니다.")
        );

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        String userImg = decodedJWT
                .getClaim(CLAIM_USER_PROFILE_IMG)
                .asString();
//        System.out.println("Long 파서:" + userImg);

        return userImg;
    }

    public String decodeUserEmail(String token) {
        DecodedJWT decodedJWT = isValidToken(token).orElseThrow(
                () -> new IllegalArgumentException("유효한 토큰이 아닙니다.")
        );

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        String email = decodedJWT
                .getClaim(CLAIM_USER_EMAIL)
                .asString();

        return email;
    }
    public String decodeUserNickname(String token) {
        DecodedJWT decodedJWT = isValidToken(token).orElseThrow(
                () -> new IllegalArgumentException("유효한 토큰이 아닙니다.")
        );

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        String nickname = decodedJWT
                .getClaim(CLAIM_USER_NAME)
                .asString();

        return nickname;
    }
    public AccountRoleEnum decodeUserRole(String token) {
        DecodedJWT decodedJWT = isValidToken(token).orElseThrow(
                () -> new IllegalArgumentException("유효한 토큰이 아닙니다.")
        );

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        String Role = decodedJWT
                .getClaim(CLAIM_USER_ROLE)
                .asString();
        AccountRoleEnum accountRoleEnum = AccountRoleEnum.valueOf(Role);

        return accountRoleEnum;
    }


    public Optional<DecodedJWT> isValidToken(String token) {
        DecodedJWT jwt = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .build();

            jwt = verifier.verify(token);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return Optional.ofNullable(jwt);
    }
}