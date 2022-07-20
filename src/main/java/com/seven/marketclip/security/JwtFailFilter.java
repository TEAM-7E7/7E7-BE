package com.seven.marketclip.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFailFilter implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("진입 성공");
        response.setCharacterEncoding("utf-8");
        response.setStatus(400);
        response.getWriter().print("JWT 문제 생김");
//        if(exception.getMessage().equals("RefreshFilter-Refresh-invalid_expired_not_existDB")){
//            System.out.println("리프레쉬 필터 - 리프레쉬 토큰 - 잘못됨");
//            response.getWriter().println(exception.getMessage());
//
//        }else if(exception.getMessage().equals("AccessToken - No Header")){
//            System.out.println("리프레쉬 필터 - 리프레쉬 토큰 - 잘못됨");
//            response.getWriter().println(exception.getMessage());
//
//        }else if(){
//
//        }

    }
}
