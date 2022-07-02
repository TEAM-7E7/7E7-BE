package com.seven.marketclip.account.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

//    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // userRequest 는 code를 받아서 accessToken을 응답 받은 객체
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // google의 회원 프로필 조회

        // code를 통해 구성한 정보
        System.out.println("asd : " + userRequest);
        System.out.println("userRequest clientRegistration : " + userRequest.getClientRegistration());
        // token을 통해 응답받은 회원정보
        System.out.println("oAuth2User : " + oAuth2User);
        System.out.println("oAuth2User : " + oAuth2User.getAttributes());
        return processOAuth2User(userRequest, oAuth2User);
//        return null;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        System.out.println("유저 리퀘스트 : " + userRequest);
        System.out.println("오스 유저 : " + oAuth2User);

        // Attribute를 파싱해서 공통 객체로 묶는다. 관리가 편함.
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청~~");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            System.out.println(oAuth2UserInfo.toString());
            GoogleUserInfo sad = (GoogleUserInfo)oAuth2UserInfo;
            sad.printAttribute();
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            System.out.println("네이버 로그인 요청~~");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else {
            System.out.println("우리는 구글과 네이버만 지원해요 ㅎㅎ");
        }

        //System.out.println("oAuth2UserInfo.getProvider() : " + oAuth2UserInfo.getProvider());
        // System.out.println("oAuth2UserInfo.getProviderId() : " + oAuth2UserInfo.getProviderId());
        //
        //각각의 소셜ID 함수에 앞에 카카오인지
        String randomNickname = RandomStringUtils.random(8, true, true);
        Account account;
        if(accountRepository.existsByEmail(oAuth2UserInfo.getEmail()) || accountRepository.existsByNickname(randomNickname)){

            System.out.println("규굴, 네이 버 사용자 회원가입 불가 - 이메일,닉네임 중 이미 있음");
            //이미 있으니까 바로 로그인인
            account =  accountRepository.findByEmail(oAuth2UserInfo.getSocialId()).orElseThrow(
                    ()-> new IllegalArgumentException("존재하는 아이디가 없습니다.")
            );
        }else {//이메일과 닉네임이 둘다 존재하지 않을 때
            String uuidPassword = String.valueOf(UUID.randomUUID());
            AccountRoleEnum roleEnum = AccountRoleEnum.USER;
            account = Account.builder()
                    .nickname(randomNickname)
                    .email(oAuth2UserInfo.getEmail())
                    .password(uuidPassword)
                    .type(oAuth2UserInfo.getRole())
                    .role(roleEnum)
                    .build();
            account.EncodePassword(bCryptPasswordEncoder);
            accountRepository.save(account);
        }

        return new UserDetailsImpl(account.getEmail(), account.getRole());
    }
}