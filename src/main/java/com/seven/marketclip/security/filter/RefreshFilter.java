package com.seven.marketclip.security.filter;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.security.FormLoginSuccessHandler;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.security.jwt.HeaderTokenExtractor;
import com.seven.marketclip.security.jwt.JwtDecoder;
import com.seven.marketclip.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;


@RequiredArgsConstructor
@WebFilter(urlPatterns= "/api/refresh-re")
public class RefreshFilter implements Filter {

    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;
    private final AccountRepository accountRepository;


    //리프레쉬 토큰 O, JWT토큰 X 일 때
    //JWT토큰으로 리프레쉬 토큰과 JWT 재발급.
    //리프레쉬 토큰만 받으면
    //무조건 둘다 새로 발급되니까 문제 있는거 아닌가?
    @Transactional
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("리프레쉬 토큰 필터");

        //헤더에 토큰이 잇는지 확인.
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String refresh = httpServletRequest.getHeader("X-REFRESH-TOKEN");

        if(refresh == null){
            System.out.println("헤더가 없음");
            return;
        }

        //TODO Decoder에 있는거 같은 함수로 빼기
        //올바른 토큰인지 확인
        refresh = headerTokenExtractor.extract(refresh, httpServletRequest);
        System.out.println("리프레쉬 토큰 확인");
        System.out.println("헤더에서 받은 jwt 확인 " + refresh);


        //DB에서 확인
        if(!accountRepository.existsByRefreshToken(refresh)){
            System.out.println("DB에서 확인 불가 ");
            return;
        }

        //만료된 토큰인지 확인 -> JWT필터에서도 해줘야함.
        Long id = jwtDecoder.decodeUserId(refresh); //여기 안에서 만료됐는지 확인.
        Account account = accountRepository.findById(id).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 아이디")
        );
        UserDetailsImpl userDetails= new UserDetailsImpl(account.getId(), account.getPassword(),account.getNickname(),account.getRole());


        //JWT토큰과 Refresh 토큰 재발급
        final String reissuanceJWT = JwtTokenUtils.generateJwtToken(userDetails);
        final String reissuanceRefreshToken = JwtTokenUtils.generateRefreshToken(userDetails);
        account.changeRefreshToken(reissuanceRefreshToken);

        httpServletResponse.addHeader(FormLoginSuccessHandler.JWT_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceJWT);
        httpServletResponse.addHeader(FormLoginSuccessHandler.REFRESH_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceRefreshToken);
        System.out.println("리프레시 필터 끝");
    }
    
}
