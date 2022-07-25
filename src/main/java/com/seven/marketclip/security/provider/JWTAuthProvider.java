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
//        System.out.println("전체필터 3");
        String token = (String) authentication.getPrincipal();

        Long id = jwtDecoder.decodeUserId(token);
        String nickname = jwtDecoder.decodeUserNickname(token);
        String email = jwtDecoder.decodeUserEmail(token);
        String img = jwtDecoder.decodeImg(token);
        AccountRoleEnum role = jwtDecoder.decodeUserRole(token);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .profileImgUrl(img)
                .role(role)
                .build();

//        System.out.println("전체필터 4");
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
}