package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.repository.AccountRepository;
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
        response.getWriter().print("SOCIAL_LOGIN_FAIL-EMAIL_ALREADY_EXIST");
        response.setStatus(400);
        response.sendRedirect("https://marketclip.kr?social=EMAIL_ALREADY_EXIST");
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        super.setRedirectStrategy(redirectStrategy);
    }
}