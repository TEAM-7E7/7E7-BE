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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class AccountController {

    //    private final AccountReqDtoValidation accountReqDtoValidation;
    private final AccountService accountService;

    @ApiOperation(value = "회원가입", notes = "회원가입 하는 API")
    @PostMapping("/sign-up")
    public ResponseEntity<HttpResponse> signUp(@RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.addUser(accountReqDTO));
    }

    @ApiOperation(value = "닉네임 중복확인", notes = "닉네임 중복 확인하는 API")
    @PostMapping("/nickname-check")
    public ResponseEntity<HttpResponse> nicknameCheck(@RequestBody Map<String, String> map) {
        String nickname = map.get("nickname");
        return HttpResponse.toResponseEntity(accountService.checkNickname(nickname));
    }

    @ApiOperation(value = "프로필 이미지 파일 S3 업로드", notes = "게시글 이미지 파일 S3 저장 api")
    @PostMapping(value = "/profile-img")
    public ResponseEntity<HttpResponse> s3AddUserImage(@RequestParam("userProfile") MultipartFile multipartFile, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(accountService.addS3UserImage(multipartFile, userDetails.getId()));
    }

    @ApiOperation(value = "프로필 이미지 삭제", notes = "회원 프로필 사진 삭제하기")
    @DeleteMapping("/profile-img")
    public ResponseEntity<HttpResponse> deleteProfileImg(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(accountService.profileImgDelete(userDetails.getId()));
    }

    //프로필 닉네임 수정
    @ApiOperation(value = "프로필 닉네임 수정", notes = "회원 프로필 닉네임 수정하기")
    @PostMapping("/profile-nickname")
    public ResponseEntity<HttpResponse> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String nickname) {
        return HttpResponse.toResponseEntity(accountService.updateNickname(userDetails.getId(), nickname));
    }

    //비밀번호 변경
    @ApiOperation(value = "프로필 비밀번호 수정", notes = "회원 프로필 비밀번호 수정하기")
    @PostMapping("/profile-password")
    public ResponseEntity<HttpResponse> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String password) {
//        SecurityContextHolder.getContext().setAuthentication((Authentication) userDetails);
        return HttpResponse.toResponseEntity(accountService.updatePassword(userDetails.getId(), password));
    }

    // 비밀번호 찾기 -> 해당 이메일로 인증번호 전송
    @ApiOperation(value = "비밀번호 찾기 기능", notes = "회원 프로필 비밀번호 찾기")
    @PostMapping("/profile-find-password")
    public ResponseEntity<HttpResponse> findPassword(@RequestParam String email) {
//        SecurityContextHolder.getContext().setAuthentication((Authentication) userDetails);
        return HttpResponse.toResponseEntity(accountService.findPassword(email));
    }

}
