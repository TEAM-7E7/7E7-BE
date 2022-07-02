package com.seven.marketclip.email;

import com.seven.marketclip.exception.HttpErrorResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;

    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender) {
        this.emailRepository = emailRepository;
        this.javaMailSender = javaMailSender;
    }

    @Transactional
    public void checkEmail(EmailDTO emailDTO) throws RuntimeException {
        /**
         * 1. (토큰없이 이메일 데이터로만 API호출) 이메일 객체가 없을 때 - 이메일 객체 생성
         * 2. (토큰없이 이메일 데이터로만 API호출) 이메일 객체가 있고 생성 후 10분이 지나지 않았을 때 - "이미 이메일이 발송되었습니다"
         * 2. 인증코드 재발송 (방법 논의) - authToken만 새로 생성
         *
         * 3. (토큰과 이메일 데이터로 API호출) 이메일 객체가 없을 때 - "이메일 인증시간이 지났습니다, 다시 인증번호를 발급해주세요"
         * 4. (토큰과 이메일 데이터로 API호출) 이메일 객체가 있지만 토큰이 다를 경우 - "인증번호가 일치하지 않습니다"
         *
         * 5. 이메일 객체 생성 후 10분이 지났을 때 - 객체 삭제
         */

        if (emailRepository.findByUserEmail(emailDTO.getEmail()).isEmpty()) {
            String authToken = RandomStringUtils.random(8, true, true);
            sendEmail(emailDTO.getEmail(), authToken);
            Email email = Email.builder()
                    .userEmail(emailDTO.getEmail())
                    .authToken(authToken)
                    .build();

            emailRepository.save(email);
        } else {
            Optional<Email> email = emailRepository.findByUserEmail(emailDTO.getEmail());
        }
    }

    // 두시간마다 폐기된 이메일 데이터 삭제
    @Scheduled(cron = "0 0 0/2 * * *")
    public void deleteEmail() {
        LocalDateTime localDateTime = LocalDateTime.now();
        emailRepository.deleteAllByExpireDateBefore(localDateTime);
    }

    public void sendEmail(String email, String authToken) {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(email);
        simpleMessage.setSubject("marketClip 이메일 인증");
//        simpleMessage.setText("http://localhost:8080/api/sign/confirm-email?email="+email+"&authToken="+authToken);
        simpleMessage.setText("이메일 인증번호=" + authToken);
        javaMailSender.send(simpleMessage);
    }

}
