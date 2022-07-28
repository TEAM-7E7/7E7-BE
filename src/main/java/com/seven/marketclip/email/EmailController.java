package com.seven.marketclip.email;

import com.seven.marketclip.exception.HttpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/email")
@Api(tags = "이메일 인증 컨트롤러")
@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @ApiOperation(value = "이메일 체크", notes = "이메일을 인증하는 API")
    @PostMapping("/verification")
    public ResponseEntity<HttpResponse> emailCheck(@RequestBody EmailDTO emailDTO) {
        return HttpResponse.toResponseEntity(emailService.checkEmail(emailDTO));
    }


    // 비밀번호 찾기 (이메일)
    @ApiOperation(value = "비밀번호 찾기를 위한 이메일 체크", notes = "")
    @PostMapping("/password-search")
    public ResponseEntity<HttpResponse> searchPassword(@RequestBody EmailDTO emailDTO) {
        return HttpResponse.toResponseEntity(emailService.findPassword(emailDTO));
    }

}
