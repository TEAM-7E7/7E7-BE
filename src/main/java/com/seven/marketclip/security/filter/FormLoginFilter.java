package com.seven.marketclip.security.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormLoginFilter extends UsernamePasswordAuthenticationFilter {
    final private ObjectMapper objectMapper;

    public FormLoginFilter(final AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("로그인 필터 1");

//         SecurityContextHolder.getContext().getAuthentication().getPrincipal()

        UsernamePasswordAuthenticationToken authRequest;
        try {
            JsonNode requestBody = objectMapper.readTree(request.getInputStream());
            String email = requestBody.get("email").asText(); //email
            String password = requestBody.get("password").asText();
            authRequest = new UsernamePasswordAuthenticationToken(email, password); //사용자가 입력한 아이디(이메일) 비번.
        } catch (Exception e) {
            throw new RuntimeException("username, password 입력이 필요합니다. (JSON)");
        }

        setDetails(request, authRequest);
        System.out.println("로그인 필터 2");
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
