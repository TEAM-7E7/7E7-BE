package com.seven.marketclip.security.provider;

import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.security.jwt.JwtDecoder;
import com.seven.marketclip.security.jwt.JwtPreProcessingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTAuthProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;

    private final AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("전체필터 3");
        String token = (String) authentication.getPrincipal();

        Long id = jwtDecoder.decodeUserId(token);
        String nickname = jwtDecoder.decodeUserNickname(token);
        AccountRoleEnum role = jwtDecoder.decodeUserRole(token);

        // TODO: API 사용시마다 매번 User DB 조회 필요
        //  -> 해결을 위해서는 UserDetailsImpl 에 User 객체를 저장하지 않도록 수정
        //  ex) UserDetailsImpl 에 userId, username, role 만 저장
        //    -> JWT 에 userId, username, role 정보를 암호화/복호화하여 사용

        UserDetailsImpl userDetails = new UserDetailsImpl(id,nickname,role);

        System.out.println("전체필터 4");
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
}
