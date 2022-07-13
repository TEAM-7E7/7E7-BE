package com.seven.marketclip.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.security.jwt.HeaderTokenExtractor;
import com.seven.marketclip.security.jwt.JwtDecoder;
import com.seven.marketclip.security.jwt.JwtPreProcessingToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static com.seven.marketclip.security.jwt.JwtTokenUtils.*;

/**
 * Token 을 내려주는 Filter 가 아닌  client 에서 받아지는 Token 을 서버 사이드에서 검증하는 클레스 SecurityContextHolder 보관소에 해당
 * Token 값의 인증 상태를 보관 하고 필요할때 마다 인증 확인 후 권한 상태 확인 하는 기능
 */
public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtDecoder jwtDecoder;
    private final HeaderTokenExtractor extractor;
    private final AccountRepository accountRepository;

    public JwtAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher, HeaderTokenExtractor extractor, JwtDecoder jwtDecoder, AccountRepository accountRepository) {
        super(requiresAuthenticationRequestMatcher);
        this.extractor = extractor;
        this.jwtDecoder = jwtDecoder;
        this.accountRepository = accountRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        System.out.println("전체필터 1");
        // JWT 값을 담아주는 변수 TokenPayload
        String authorization = request.getHeader("X-ACCESS-TOKEN");
        String refreshToken = request.getHeader("X-REFRESH-TOKEN");

        System.out.println("전체필터 헤더값 : " + authorization);
        try{
            System.out.println(authorization.length());
        }catch (Exception e){
            response.getWriter().print("RefreshFilter-Refresh-invalid_expired_not_existDB");
            response.setStatus(400);
            throw new IllegalArgumentException("리프레쉬 필터 - 리프레쉬 토큰 - 잘못됨");
        }

        System.out.println(refreshToken.length());
//        JwtPreProcessingToken jwtToken = checkValidJwtToken(request, authorization, refreshToken);

        if (authorization == null || authorization.length() == 0) {
            response.getWriter().print("JwtAuthFilter-AccessToken-No_Header");
            response.setStatus(400);
            throw new IllegalArgumentException("AccessToken - No Header");
//            return null;
        }
        if (refreshToken == null || refreshToken.length() == 0) {
            response.getWriter().print("JwtAuthFilter-Refresh-No_Header");
            response.setStatus(400);
            throw new IllegalArgumentException("RefreshToken - No Header");
//            return null;
        }


        //JWT토큰
        String jwtToken = extractor.extract(authorization, request, response);


        DecodedJWT jwt = null;

            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .build();
            try{
                jwt = verifier.verify(jwtToken);
            }catch (Exception e){
                response.getWriter().print("JwtAuthFilter-Access-Invalid");
                response.setStatus(400);
                throw new IllegalArgumentException("JWT 필터 - 형식? 개수 잘못됨 .");
            }


            Date expiredDates = jwt
                    .getClaim(CLAIM_EXPIRED_DATE)
                    .asDate();

            Date nows = new Date();
            if (expiredDates.before(nows)) {
                response.getWriter().print("JwtAuthFilter-Access-expired");
                response.setStatus(400);
                throw new IllegalArgumentException("JWT 필터 - JWT 토큰 만료됨.");
            }
        JwtPreProcessingToken jwtTokens = new JwtPreProcessingToken(extractor.extract(authorization, request, response));


        //리프레쉬 토큰
        String refresh = extractor.extract(refreshToken, request,response);
//        DecodedJWT decodedJWT = jwtDecoder.isValidToken(refresh).orElseThrow(
//                () -> new IllegalArgumentException("유효한 토큰이 아닙니다.")
//        );
        Optional<DecodedJWT> decodedJWTs = jwtDecoder.isValidToken(refresh);
        if (decodedJWTs.isEmpty()) {
            response.getWriter().print("JwtAuthFilter-Refresh-Invalid");
            response.setStatus(400);
            throw new IllegalArgumentException("리프레쉬 토큰 - 유효한 토큰이 아닙니다.");
//            return null;
        }
        DecodedJWT decodedJWT = decodedJWTs.get();
        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            response.getWriter().print("JwtAuthFilter-Refresh-expired");
            response.setStatus(400);
            throw new IllegalArgumentException("리프레쉬 토큰 - 기간 만료.");
//            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        if (!accountRepository.existsByRefreshToken(refresh)) {
            System.out.println(refresh);
            System.out.println("RefreshToken - Not ExistDB");
            response.getWriter().print("JwtAuthFilter-Refresh-Not_existDB");
            response.setStatus(400);
            throw new IllegalArgumentException("리프레쉬 토큰 - 데이터 베이스에 없음.");
        }

        System.out.println("전체필터 2");
        return super.getAuthenticationManager().authenticate(jwtTokens);
    }


//    private JwtPreProcessingToken checkValidJwtToken(HttpServletRequest request, String authorization, String refreshToken) {
//
//
//    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            @NotNull FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("전체필터 5-1");
        /*
         *  SecurityContext 사용자 Token 저장소를 생성합니다.
         *  SecurityContext 에 사용자의 인증된 Token 값을 저장합니다.
         */
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        // FilterChain chain 해당 필터가 실행 후 다른 필터도 실행할 수 있도록 연결실켜주는 메서드
        chain.doFilter(
                request,
                response
        );
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        System.out.println("전체필터 5-1-1");
        /*
         *	로그인을 한 상태에서 Token값을 주고받는 상황에서 잘못된 Token값이라면
         *	인증이 성공하지 못한 단계 이기 때문에 잘못된 Token값을 제거합니다.
         *	모든 인증받은 Context 값이 삭제 됩니다.
         */
        SecurityContextHolder.clearContext();

        super.unsuccessfulAuthentication(
                request,
                response,
                failed
        );
    }
}