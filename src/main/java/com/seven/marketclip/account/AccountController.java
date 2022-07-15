package com.seven.marketclip.account;

import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.service.AccountService;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
public class AccountController {

//    private final AccountReqDtoValidation accountReqDtoValidation;
    private final AccountService accountService;

    @ApiOperation(value = "회원가입", notes = "회원가입 하는 API")
    @PostMapping("/api/sign-up")
    public ResponseEntity<HttpResponse> signUp(@RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.addUser(accountReqDTO));
    }

    @ApiOperation(value = "닉네임 중복확인", notes = "닉네임 중복 확인하는 API")
    @PostMapping("/api/nickname-check")
    public ResponseEntity<HttpResponse> nicknameCheck(@RequestBody Map<String, String> map) {
        String nickname = map.get("nickname");
        return HttpResponse.toResponseEntity(accountService.checkNickname(nickname));
    }

    //TODO @RequestParam으로 받는게 아니라 @Authentication으로 받는게 좋을 듯?
    //TODO JWT Provider에서 디코더를 할 때 백에서는 id만? 디코더 해도 좋을 듯?
    //프로필 사진 수정 -> 이것도 가져오기 JWT에서
    @ApiOperation(value = "프로필 이미지 수정", notes = "회원 프로필 사진 수정하기")
    @PostMapping("/api/profile-img")
    public ResponseEntity<HttpResponse> updateProfileImg(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> imageMap) {
        System.out.println("프로필 이미지 수정 ID= " + userDetails.getId());
        System.out.println("프로필 이미지 수정 nickname = " + userDetails.getNickname());
        System.out.println("프로필 이미지 수정 password = " + userDetails.getPassword());
        System.out.println("프로필 이미지 수정 email = " + userDetails.getUsername());
        System.out.println("프로필 이미지 수정 role = " + userDetails.getRole());
        System.out.println("프로필 이미지 수정2  = " + imageMap.get("profileImage"));
        return HttpResponse.toResponseEntity(accountService.updateProfileImg(userDetails.getId(), imageMap.get("profileImage")));
    }
    //프로필 닉네임 수정
    @ApiOperation(value = "프로필 닉네임 수정", notes = "회원 프로필 닉네임 수정하기")
    @PostMapping("/api/profile-nickname")
    public ResponseEntity<HttpResponse> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestParam String nickname) {
        return HttpResponse.toResponseEntity(accountService.updateNickname(userDetails.getId(),nickname));
    }
    //비밀번호 변경
    @ApiOperation(value = "프로필 비밀번호 수정", notes = "회원 프로필 비밀번호 수정하기")
    @PostMapping("/api/profile-password")
    public ResponseEntity<HttpResponse> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String password){
//        SecurityContextHolder.getContext().setAuthentication((Authentication) userDetails);
        return HttpResponse.toResponseEntity(accountService.updatePassword(userDetails.getId(),password));
    }

}
