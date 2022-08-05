package com.seven.marketclip.account.oauth;

import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.account.repository.AccountRepository;
import com.seven.marketclip.account.repository.AccountRoleEnum;
import com.seven.marketclip.account.repository.AccountTypeEnum;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.image.domain.AccountImage;
import com.seven.marketclip.image.repository.AccountImageRepository;
import com.seven.marketclip.image.service.ImageService;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.seven.marketclip.exception.ResponseCode.USER_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    //    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ImageService imageService;

    private final AccountImageRepository accountImageRepository;

    // userRequest 는 code를 받아서 accessToken을 응답 받은 객체
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // google의 회원 프로필 조회

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User){

        // Attribute를 파싱해서 공통 객체로 묶는다. 관리가 편함.
        OAuth2UserInfo oAuth2UserInfo = null;
        String randomNickname = null;
        //        String randomNickname = RandomStringUtils.random(11, true, true);
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            GoogleUserInfo sad = (GoogleUserInfo) oAuth2UserInfo;
            randomNickname = "구글 ";
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            randomNickname = "네이버 ";
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            randomNickname = "카카오 ";
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            KakaoUserInfo sad = (KakaoUserInfo) oAuth2UserInfo;
        } else {
            System.out.println("우리는 구글과 네이버만 지원");
        }

        //각각의 소셜ID 함수에 앞에 카카오인지


        Optional<Account> accountOptEmail = accountRepository.findByEmail(oAuth2UserInfo.getEmail());
        Optional<Account> accountOptNickname = accountRepository.findByNickname(randomNickname);

        AccountTypeEnum typeKakao = AccountTypeEnum.KAKAO;
        AccountTypeEnum typeGoogle = AccountTypeEnum.GOOGLE;
        AccountTypeEnum typeNaver = AccountTypeEnum.NAVER;

        // 1. 로그인 하는 경우
        //    이메일이 있는경우 -> 소셜인 경우
        // 2. 회원가입 하는 경우
        //    이메일이 없고 -> 소셜인 경우

        if (accountOptEmail.isPresent() && (accountOptEmail.get().getType() == typeKakao || accountOptEmail.get().getType() == typeGoogle || accountOptEmail.get().getType() == typeNaver)) {
            //이미 있으니까 바로 로그인인
            Account account = accountOptEmail.orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );

            Long id = account.getId();
            String email = account.getEmail();
            AccountRoleEnum role = account.getRole();
            String nickname = account.getNickname();
            String imgUrl = account.getProfileImgUrl().getImageUrl();
            System.out.println(imgUrl);
            return UserDetailsImpl.builder()
                    .id(id)
                    .email(email)
                    .nickname(nickname)
                    .profileImgUrl(imgUrl)
                    .type(account.getType())
                    .role(role)
                    .build();

        } else if (accountOptEmail.isPresent()) {//마켓클립일 때 -> 예외처리
            OAuth2Error oauth2Error = new OAuth2Error("에러당!",
                    "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        } else {
            String uuidPassword = String.valueOf(UUID.randomUUID());
            AccountRoleEnum roleEnum = AccountRoleEnum.USER;
            Account account = Account.builder()
                    .nickname(null)
                    .email(oAuth2UserInfo.getEmail())
                    .password(uuidPassword)
                    .type(oAuth2UserInfo.getRole())
                    .role(roleEnum)
                    .build();
            accountRepository.save(account);
            account.encodePassword(bCryptPasswordEncoder);
            account.changeNickname(randomNickname + account.getId());



            Account account1 = accountRepository.findByEmail(oAuth2UserInfo.getEmail()).orElseThrow(
                    () -> new IllegalArgumentException("아이디를 찾을 수 없읍니다.")
            );

            accountImageRepository.save(AccountImage.builder()
                            .account(account1)
                            .imageUrl("default")
                    .build());

            return UserDetailsImpl.builder()
                    .id(account1.getId())
                    .email(account1.getEmail())
                    .nickname(account1.getNickname())
                    .profileImgUrl("default")
                    .type(account1.getType())
                    .role(account1.getRole())
                    .build();
        }
    }
}