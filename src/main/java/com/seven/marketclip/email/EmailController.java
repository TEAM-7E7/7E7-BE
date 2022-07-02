package com.seven.marketclip.email;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-validation")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    @ExceptionHandler
    public void emailCheck(@RequestBody EmailDTO emailDTO) {
        emailService.checkEmail(emailDTO);
    }

}
