package com.seven.marketclip.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
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
import java.util.Date;
import java.util.Optional;

import static com.seven.marketclip.security.jwt.JwtTokenUtils.*;


@RequiredArgsConstructor
@WebFilter(urlPatterns = "/api/refresh-re")
public class RefreshFilter implements Filter {

    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;
    private final AccountRepository accountRepository;


    //리프레쉬 토큰 O, JWT토큰 X 일 때
    //JWT토큰으로 리프레쉬 토큰과 JWT 재발급.
    //리프레쉬 토큰만 받으면
    //무조건 둘다 새로 발급되니까 문제 있는거 아닌가?
    //덕히님이 프론트에서 완전히 옳은 값만 -> 기간이 만료 안된 리프레쉬!!
    @Transactional
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("리프레쉬 토큰 필터");

        //헤더에 토큰이 잇는지 확인.
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String refresh = httpServletRequest.getHeader("X-REFRESH-TOKEN");

        if (refresh == null) {
            httpServletResponse.getWriter().println("RefreshFilter-Refresh-No_Header");
            httpServletResponse.setStatus(400);
            System.out.println("헤더가 없음");
            throw new IllegalArgumentException("리프레쉬 토큰 - 헤더가 존재하지 않습니다.");
        }

        //TODO Decoder에 있는거 같은 함수로 빼기
        //올바른 토큰인지 확인
        refresh = headerTokenExtractor.extract(refresh, httpServletRequest, httpServletResponse);
        System.out.println("리프레쉬 토큰 확인");
        System.out.println("헤더에서 받은 jwt 확인 " + refresh);


        //만료된 토큰인지 확인 -> JWT필터에서도 해줘야함.
//        Long id = jwtDecoder.decodeUserId(refresh); //여기 안에서 만료됐는지 확인.
        DecodedJWT jwt = null;
        Long id = null;

        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        JWTVerifier verifier = JWT
                .require(algorithm)
                .build();

        jwt = verifier.verify(refresh);

        Date expiredDate = jwt
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            httpServletResponse.getWriter().println("RefreshFilter - Refresh - expired");
            httpServletResponse.setStatus(400);
            throw new IllegalArgumentException("리프레쉬 필터 - 리프레쉬 토큰 만료됨.");
        }

        id = jwt
                .getClaim(CLAIM_USER_ID)
                .asLong();

        Optional<Account> accounts = accountRepository.findById(id);
        if(accounts.isEmpty()){
            httpServletResponse.getWriter().println("RefreshFilter - ID - Not exist");
            httpServletResponse.setStatus(400);
            throw new IllegalArgumentException("리프레쉬 필터 - 아이디 존재하지 않음.");
        }
        Account account = accounts.get();

        if (!refresh.equals(account.getRefreshToken())) {
            httpServletResponse.getWriter().println("RefreshToken - Not ExistDB");
            httpServletResponse.setStatus(400);
            throw new IllegalArgumentException("리프레쉬 토큰 - 데이터 베이스에 없음.");
        }

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(account.getId())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .profileImgUrl(account.getProfileImgUrl())
                .role(account.getRole())
                .build();


        //JWT토큰과 Refresh 토큰 재발급
        final String reissuanceJWT = JwtTokenUtils.generateJwtToken(userDetails);
        final String reissuanceRefreshToken = JwtTokenUtils.generateRefreshToken(userDetails);
        account.changeRefreshToken(reissuanceRefreshToken);

        httpServletResponse.addHeader(FormLoginSuccessHandler.JWT_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceJWT);
        httpServletResponse.addHeader(FormLoginSuccessHandler.REFRESH_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceRefreshToken);
        System.out.println("리프레시 필터 끝");

    }

}