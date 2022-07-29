package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.account.repository.AccountRepository;
import com.seven.marketclip.security.FormLoginSuccessHandler;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Transactional
@RequiredArgsConstructor
@Component
public class OauthHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AccountRepository accountRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("소셜 로그인 성공했을 때");
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();
        System.out.println(userDetailsImpl);
        System.out.println(userDetailsImpl.getUsername());
        System.out.println(userDetailsImpl.getId());
//        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(account.getId(), account.getEmail(), account.getRole());
//        UserDetails userDetails = userDetailsImpl;
        authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        final String refresh = JwtTokenUtils.generateRefreshToken(userDetailsImpl);


        Account account = accountRepository.findByEmail(userDetailsImpl.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("sdsfdhtm 오스 핸들러ㅜ  아읻 업음")
        );

        account.changeRefreshToken(refresh);

//        RedirectAttributes re;
//        re.addAllAttributes("","");

        response.addHeader(FormLoginSuccessHandler.JWT_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + token);
        response.addHeader(FormLoginSuccessHandler.REFRESH_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + refresh);
//        response.sendRedirect("http://localhost:3000?X-ACCESS-TOKEN="+FormLoginSuccessHandler.TOKEN_TYPE + " " + token+"&"+"X-REFRESH-TOKEN="+FormLoginSuccessHandler.TOKEN_TYPE + " " + refresh);
//        response.sendRedirect("https://marketclip.kr?X-ACCESS-TOKEN="+FormLoginSuccessHandler.TOKEN_TYPE + " " + token+"&"+"X-REFRESH-TOKEN="+FormLoginSuccessHandler.TOKEN_TYPE + " " + refresh);

        getRedirectStrategy().sendRedirect(request,response,"http://localhost:3000");
//        RedirectAttributes redirectAttributes = new
//        getR

    }
}