package com.seven.marketclip.account;

import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.service.AccountService;
import com.seven.marketclip.email.EmailDTO;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class AccountController {

    private final AccountService accountService;

    @ApiOperation(value = "회원가입", notes = "")
    @PostMapping("/sign-up")
    public ResponseEntity<HttpResponse> signUp(@RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.addUser(accountReqDTO));
    }

    @ApiOperation(value = "닉네임 중복확인", notes = "중복 확인 버튼 + 회원 가입 버튼")
    @PostMapping("/nickname-check")
    public ResponseEntity<HttpResponse> nicknameCheck(@RequestBody Map<String, String> map) {
        String nickname = map.get("nickname");
        return HttpResponse.toResponseEntity(accountService.checkNickname(nickname));
    }

    @ApiOperation(value = "프로필 이미지 파일 S3 업로드", notes = "")
    @PostMapping(value = "/profile-img")
    public ResponseEntity<HttpResponse> s3AddUserImage(@RequestParam("userProfile") MultipartFile multipartFile, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(accountService.addS3UserImage(multipartFile, userDetails.getId()));
    }

    @ApiOperation(value = "프로필 이미지 삭제", notes = "물리적 삭제 X 논리적 삭제 O")
    @DeleteMapping("/profile-img")
    public ResponseEntity<HttpResponse> deleteProfileImg(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(accountService.profileImgDelete(userDetails.getId()));
    }

    // 닉네임 수정
    @ApiOperation(value = "닉네임 변경", notes = "회원 닉네임 수정하기")
    @PutMapping("/nickname-update")
    public ResponseEntity<HttpResponse> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("nickname") String nickname) {
        return HttpResponse.toResponseEntity(accountService.updateNickname(userDetails.getId(), nickname));
    }

    // 비밀번호 찾기 (이메일)
    @ApiOperation(value = "비밀번호 찾기 신청", notes = "회원 비밀번호 찾기 신청")
    @PostMapping("/password-search")
    public ResponseEntity<HttpResponse> searchPassword(@RequestBody EmailDTO emailDTO) {
        return HttpResponse.toResponseEntity(accountService.findPassword(emailDTO));
    }

    // 비밀번호 변경 (이메일)
    @ApiOperation(value = "비밀번호 찾기 신청", notes = "회원 비밀번호 찾기 신청 후 변경")
    @PutMapping("/password-search")
    public ResponseEntity<HttpResponse> changePassword(@RequestBody Map<String, String> emailPassword) {
        return HttpResponse.toResponseEntity(accountService.changePassword(emailPassword.get("email"), emailPassword.get("password")));
    }

    // 비밀번호 변경 (로그인)
    @ApiOperation(value = "비밀번호 변경", notes = "회원 비밀번호 수정하기")
    @PutMapping("/password-update")
    public ResponseEntity<HttpResponse> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("password") String password) {
        return HttpResponse.toResponseEntity(accountService.updatePassword(userDetails, password));
    }

    @ApiOperation(value = "회원 탈퇴", notes = "")
    @DeleteMapping("/sign-out")
    public ResponseEntity<HttpResponse> signOut(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(accountService.deleteUser(userDetails.getId()));
    }

    @ApiOperation(value = "리프레쉬 토큰 재발급", notes = "리프레쉬 토큰 재발급")
    @GetMapping("/refresh-re")
    public ResponseEntity<HttpResponse> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return HttpResponse.toResponseEntity(accountService.reissueRefreshToken(request, response));
    }

}
