package com.seven.marketclip.account;

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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
