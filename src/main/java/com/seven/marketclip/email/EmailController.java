package com.seven.marketclip.email;

import com.seven.marketclip.exception.HttpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/email-validation")
@Api(tags = "이메일 인증 컨트롤러")
@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @ApiOperation(value = "이메일 체크",notes = "이메일을 인증하는 API")
    @PostMapping
    public ResponseEntity<HttpResponse> emailCheck(@RequestBody EmailDTO emailDTO) {
        return emailService.checkEmail(emailDTO);
    }

//    public void emailCheck(@RequestBody EmailDTO emailDTO) {
//        emailService.checkEmail(emailDTO);
//    }


}
