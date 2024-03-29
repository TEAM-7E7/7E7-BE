package com.seven.marketclip.security;

import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.account.repository.AccountRepository;
import com.seven.marketclip.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor
public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AccountRepository accountRepository;
    public static final String JWT_HEADER = "X-ACCESS-TOKEN";
    public static final String REFRESH_HEADER = "X-REFRESH-TOKEN";
    public static final String TOKEN_TYPE = "BEARER";

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {

        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());

        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        // refresh 토큰 생성
        final String refresh = JwtTokenUtils.generateRefreshToken(userDetails);


        Account account = accountRepository.findById(userDetails.getId()).orElseThrow(
                ()-> new IllegalArgumentException("찾는 아이디가 없습니다.")
        );
        account.changeRefreshToken(refresh);


        response.addHeader(JWT_HEADER, TOKEN_TYPE + " " + token);
        response.addHeader(REFRESH_HEADER, TOKEN_TYPE + " " + refresh);

    }

}
