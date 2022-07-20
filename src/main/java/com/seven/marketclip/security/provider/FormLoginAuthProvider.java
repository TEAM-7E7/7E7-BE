package com.seven.marketclip.security.provider;

import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@RequiredArgsConstructor
public class FormLoginAuthProvider implements AuthenticationProvider {

    @Resource(name="userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {

            System.out.println("로그인 필터 3");
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            // FormLoginFilter 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
            String email = token.getName();
            String password = (String) token.getCredentials();
            System.out.println("이메일:"+email);
            System.out.println("비번 : "+password);

            // UserDetailsService 를 통해 DB에서 username 으로 사용자 조회
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
            System.out.println(userDetails.getPassword());
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                System.out.println("로그인 필터 : 비번 틀림");
                throw new BadCredentialsException("Invalid password");
            }
            System.out.println("로그인 필터 6");
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
