package com.seven.marketclip.email;

import com.seven.marketclip.account.repository.AccountRepository;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final AccountRepository accountRepository;
    private final MailUtil mailUtil;

    public EmailService(EmailRepository emailRepository, AccountRepository accountRepository, MailUtil mailUtil) {
        this.emailRepository = emailRepository;
        this.accountRepository = accountRepository;
        this.mailUtil = mailUtil;
    }

    @Transactional
    public ResponseCode checkEmail(EmailDTO emailDTO) throws CustomException {
        if (accountRepository.findByEmail(emailDTO.getEmail()).isPresent()) {
            throw new CustomException(USER_ALREADY_EXISTS);
        }
        return emailResponse(emailDTO.getEmail(), emailDTO.getEmailToken());
    }

    @Transactional
    public ResponseCode findPassword(EmailDTO emailDTO) throws CustomException {
        if (accountRepository.findByEmail(emailDTO.getEmail()).isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        return emailResponse(emailDTO.getEmail(), emailDTO.getEmailToken());
    }

    @Transactional
    public void deleteEmailVerified(String email) {
        emailRepository.deleteByUserEmail(email);
    }

    public void checkVerified(String email) throws CustomException {
        Email emailFound = emailRepository.findByUserEmail(email).orElseThrow(
                () -> new CustomException(EMAIL_CHECK_NOT_FOUND)
        );
        if (!emailFound.isEmailVerified()) {
            throw new CustomException(UNVERIFIED_EMAIL);
        }
    }

    private void sendEmail(String email, String emailToken) throws CustomException {
        Map<String, Object> mappedToken = new HashMap<>();
        mappedToken.put("emailToken", emailToken);
        try {
            mailUtil.sendTemplateMail(email, "MarketClip 가입 이메일 인증", "MarketClip", mappedToken);
        } catch (Exception e) {
            throw new CustomException(EMAIL_NOT_EXIST);
        }
    }

    private ResponseCode emailResponse(String receivedEmail, String receivedToken) {
        String emailToken = RandomStringUtils.random(8, true, true);
        Optional<Email> emailOpt = emailRepository.findByUserEmail(receivedEmail);

        if (receivedToken.isEmpty()) {
            // 이메일로만 API 호출
            if (emailOpt.isEmpty()) {
                sendEmail(receivedEmail, emailToken);
                Email email = Email.builder()
                        .userEmail(receivedEmail)
                        .emailToken(emailToken)
                        .build();
                emailRepository.save(email);
            } else {
                Email email = emailOpt.get();
                sendEmail(receivedEmail, emailToken);
                email.update(LocalDateTime.now(), emailToken);
            }
            return EMAIL_DISPATCH_SUCCESS;
        } else {
            // 이메일과 이메일 토큰으로 API 호출 - receivedToken 이 DB의 emailToken 과 일치하지 않으면 throw Exception
            Email email = emailOpt.orElseThrow(
                    () -> new CustomException(EMAIL_ALREADY_EXPIRED)
            );
            if (email.checkExpired(LocalDateTime.now())) {
                throw new CustomException(EMAIL_ALREADY_EXPIRED);
            } else if (!email.getEmailToken().equals(receivedToken)) {
                throw new CustomException(INVALID_EMAIL_TOKEN);
            } else {
                // 이메일 verified를 true로 변경
                email.verified();
                return EMAIL_VALIDATION_SUCCESS;
            }
        }
    }

}
