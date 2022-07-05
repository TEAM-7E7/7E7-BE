package com.seven.marketclip.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.account.AccountTypeEnum;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.dto.KakaoOauthDTO;
import com.seven.marketclip.email.EmailService;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.security.FormLoginSuccessHandler;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.security.jwt.JwtTokenUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class AccountService {

    private final EmailService emailService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountService(EmailService emailService, AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.emailService = emailService;
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //닉네임 증복체크
    public ResponseCode checkNickname(String nickname) {
        Optional<Account> accountOpt = accountRepository.findByNickname(nickname);
        if (accountOpt.isPresent()) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
        return NICKNAME_VALIDATION_SUCCESS;
    }

    // marketClip 회원가입
    @Transactional
    public ResponseCode addUser(AccountReqDTO accountReqDTO) throws CustomException {
        String encodedPassword = bCryptPasswordEncoder.encode(accountReqDTO.getPassword());

        Account account = Account.builder()
                .email(accountReqDTO.getEmail())
                .nickname(accountReqDTO.getNickname())
                .password(encodedPassword)
                .role(AccountRoleEnum.USER)
                .type(AccountTypeEnum.MARKETCLIP)
                .build();

        if(!emailService.checkVerified(accountReqDTO.getEmail())){
            throw new CustomException(UNVERIFIED_EMAIL);
        }

        accountRepository.save(account);

        return SIGNUP_SUCCESS;
    }

    //카카오 로그인 서비스
    @Transactional
    public ResponseCode kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoOauthDTO kakaoOauthDto = getKakaoUserInfo(accessToken);

        // 3. 카카오 사용자 회원가입 and 로그인
        Account account = selectLoginType(kakaoOauthDto);

        // 4. 강제 로그인 처리
        UserDetailsImpl userDetailsImpl = forceLogin(account);

        //5. JWT 토큰 발행
        return makeToken(account, userDetailsImpl);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "5d3be34c3fc241b949c6edfc97ef478b");
        body.add("redirect_uri", "http://localhost:8080/api/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = response.getBody();
        System.out.println("제이슨 노드 - 엑세스 토큰1 "+responseBody);
        JsonNode jsonNode = objectMapper.readTree(responseBody); //get함수로 빼낼 수 있게 하려고
        System.out.println("제이슨 노드 - 엑세스 토큰2 "+jsonNode);
        return jsonNode.get("access_token").asText();
    }

    private KakaoOauthDTO getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        // HTTP Header 생성
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String id = String.valueOf(jsonNode.get("id").asLong());

        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        System.out.println("사용자 "+jsonNode);
//        String email = jsonNode.get("kakao_account")
//                .get("email").asText();

//        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        AccountTypeEnum typeEnum = AccountTypeEnum.KAKAO;
        return new KakaoOauthDTO(id, nickname, typeEnum);
    }

    private Account selectLoginType(KakaoOauthDTO kakaoOauthDto) {
        String randomNickname = RandomStringUtils.random(8, true, true);
        Optional<Account> accountOptEmail = accountRepository.findByEmail(kakaoOauthDto.getId());
        Optional<Account> accountOptNickname = accountRepository.findByNickname(kakaoOauthDto.getId());
        if(accountOptEmail.isPresent() || accountOptNickname.isPresent()){
            System.out.println("카카오 사용자 회원가입 불가 - 이메일,닉네임 중 이미 있음");

            return accountRepository.findByEmail(kakaoOauthDto.getId()).orElseThrow(
                    ()-> new IllegalArgumentException("존재하는 아이디가 없습니다.")
            );
        }

        String uuidPassword = String.valueOf(UUID.randomUUID());

        AccountRoleEnum roleEnum = AccountRoleEnum.USER;
        Account account = Account.builder()
                .nickname(randomNickname)
                .email(kakaoOauthDto.getId())
                .password(uuidPassword)
                .role(roleEnum)
                .type(kakaoOauthDto.getType())
                .build();
        account.encodePassword(bCryptPasswordEncoder.encode(uuidPassword));
        accountRepository.save(account);
        return account;
    }

    private UserDetailsImpl forceLogin(Account account) {
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(account.getId(), account.getEmail(), account.getRole());
//        UserDetails userDetails = userDetailsImpl;
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userDetailsImpl;
    }
    private ResponseCode makeToken(Account account, UserDetailsImpl userDetailsImpl) {
        final String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        final String refresh = JwtTokenUtils.generateRefreshToken(userDetailsImpl);

        account.changeRefreshToken(refresh);

        HttpHeaders headers = new HttpHeaders();

        headers.set(FormLoginSuccessHandler.JWT_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + token);
        headers.set(FormLoginSuccessHandler.REFRESH_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + refresh);
        return SIGNUP_SUCCESS;
//        return ResponseEntity.ok().body(null);
    }

}
