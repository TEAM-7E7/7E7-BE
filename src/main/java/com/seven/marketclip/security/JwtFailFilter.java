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
        response.setCharacterEncoding("utf-8");
        response.setStatus(400);
        response.getWriter().print("JWT 문제 생김");

        if(exception.getMessage().equals("AccessToken - No Header")){
            response.getWriter().print("AccessToken - No Header");
            response.setStatus(400);

        }else if(exception.getMessage().equals("JwtAuthFilter-Refresh-No_Header")){
            response.getWriter().print("JwtAuthFilter-Refresh-No_Header");
            response.setStatus(400);

        }else if(exception.getMessage().equals("JwtAuthFilter-Access-Invalid")) {
            response.getWriter().print("JwtAuthFilter-Access-Invalid");
            response.setStatus(400);

        }else if(exception.getMessage().equals("JwtAuthFilter-Access-expired")) {
            response.getWriter().print("JwtAuthFilter-Access-expired");
            response.setStatus(400);

        }else if(exception.getMessage().equals("JwtAuthFilter-Refresh-Invalid")){
            response.getWriter().print("JwtAuthFilter-Refresh-Invalid");
            response.setStatus(400);

        }else if(exception.getMessage().equals("JwtAuthFilter-Refresh-expired")){
            response.getWriter().print("JwtAuthFilter-Refresh-expired");
            response.setStatus(400);

        }else if(exception.getMessage().equals("JwtAuthFilter-Refresh-Not_existDB")){
            response.getWriter().print("JwtAuthFilter-Refresh-Not_existDB");
            response.setStatus(400);

        }

    }
}
