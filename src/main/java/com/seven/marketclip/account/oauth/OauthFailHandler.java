package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Transactional
@RequiredArgsConstructor
@Component
public class OauthFailHandler extends SimpleUrlAuthenticationFailureHandler {
    private final AccountRepository accountRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authentication) throws IOException {
        System.out.println("실패한 소셜 로그인 쪽");
        response.getWriter().print("SOCIAL_LOGIN_FAIL-ALREADY_EXIST_EMAIL");
        response.setStatus(400);
//        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        response.sendRedirect("https://marketclip.kr?social=fail");
        return;
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        super.setRedirectStrategy(redirectStrategy);
    }
}