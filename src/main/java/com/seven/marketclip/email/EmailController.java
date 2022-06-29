package com.seven.marketclip.email;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email-validation")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public String emailCheck(@RequestBody EmailDTO emailDTO) {
        return emailService.checkEmail(emailDTO);
    }

}
