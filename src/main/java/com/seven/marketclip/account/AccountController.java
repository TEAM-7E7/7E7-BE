package com.seven.marketclip.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seven.marketclip.account.service.AccountService;
import com.seven.marketclip.account.validation.AccountReqDtoValidation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountReqDtoValidation accountReqDtoValidation;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @InitBinder("AccountReqDTO")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountReqDtoValidation);
    }

    @ApiOperation(value = "홈 테스트!",notes = "홈 화면을 출력해주는 API")
    @GetMapping("/")
    ResponseEntity<String> Home(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("asd","fuchkin");
        headers.add("asdsdsds","fuchkin");
        return ResponseEntity.ok().headers(headers).body("홈");
    }

    @ApiOperation(value = "유저 권한 테스트",notes = "매니저 화면을 출력해주는 API")
    @GetMapping("/api/manager")
    ResponseEntity<String> Manager(){
        System.out.println("매니저 필터");
        return ResponseEntity.ok().body("매니저 페이지");
    }

    /*********************************************************************************
     *********************************************************************************
     *********************************************************************************/
    @ApiOperation(value = "닉네임 중복체크",notes = "닉네임 중복체크 하는 API")
    @PostMapping("/api/nickname-validated")
    ResponseEntity<?> nicknameValidation(String nickname){
        return accountService.nicknameValidation(nickname);
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
    @ApiOperation(value = "회원가입",notes = "회원가입 하는 API")
    @PostMapping("/api/sign-up")
    ResponseEntity<?> signUp(@RequestBody Account account, Errors errors){
        //회원가입
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body("올바른 형식이 아닙니다.");
        }

        AccountTypeEnum role = AccountTypeEnum.MARKETCLIP;
        account.saveAccountType(role);
        account.EncodePassword(passwordEncoder);
        accountRepository.save(account);

        return ResponseEntity.ok().body(account);
    }

    //소셜 로그인
    //KAKAO Social Login
    @GetMapping("/api/kakao/callback")
    public ResponseEntity<?> kakaoLogin(String code) throws JsonProcessingException {
        System.out.println("카카오 로그인 시작 1");
        System.out.println("코드 "+code);
        return accountService.kakaoLogin(code);
    }
    //Google
    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<?> googleLogin(String code){
        System.out.println("카카오 로그인 시작 2");
        return ResponseEntity.ok().body(code);
    }




}
