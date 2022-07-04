package com.seven.marketclip.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.service.AccountService;
import com.seven.marketclip.account.validation.AccountReqDtoValidation;
import com.seven.marketclip.exception.HttpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountReqDtoValidation accountReqDtoValidation;
    private final AccountService accountService;

    @InitBinder("AccountReqDTO")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountReqDtoValidation);
    }

//    @ApiOperation(value = "회원가입",notes = "회원가입 하는 API")
//    @PostMapping("/api/sign-up")
//    ResponseEntity<?> signUp(@Validated @RequestBody AccountReqDTO accountReqDTO, Errors errors){
//        //회원가입
//        if(errors.hasErrors()){
//            return ResponseEntity.badRequest().body("올바른 형식이 아닙니다.");
//        }
//
//        AccountTypeEnum role = AccountTypeEnum.MARKETCLIP;
//        account.saveAccountType(role);
//        account.EncodePassword(passwordEncoder);
//        accountRepository.save(account);
//
//        return ResponseEntity.ok().body(account);
//    }

    @ApiOperation(value = "회원가입", notes = "회원가입 하는 API")
    @PostMapping("/api/sign-up")
    public ResponseEntity<HttpResponse> signUp(@RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.addUser(accountReqDTO));
    }

    //소셜 로그인
    //KAKAO Social Login
    @GetMapping("/api/kakao/callback")
    public ResponseEntity<HttpResponse> kakaoLogin(String code) throws JsonProcessingException {
//        System.out.println("카카오 로그인 시작 1");
//        System.out.println("코드 " + code);
        return HttpResponse.toResponseEntity(accountService.kakaoLogin(code));
    }

    //Google
//    @GetMapping("/login/oauth2/code/google")
//    public ResponseEntity<HttpResponse> googleLogin(String code) {
//        System.out.println("구글, 네이버 로그인 시작");
//        return HttpResponse.toResponseEntity(accountService.);
//    }

}
