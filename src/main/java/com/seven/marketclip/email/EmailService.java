package com.seven.marketclip.email;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public EmailService(EmailRepository emailRepository, AccountRepository accountRepository, JavaMailSender javaMailSender) {
        this.emailRepository = emailRepository;
        this.accountRepository = accountRepository;
        this.javaMailSender = javaMailSender;
    }


    /**
     * 공통 - Account 테이블에 Email 이 존재 할 경우 - "이미 존재하는 사용자입니다." [ USER_ALREADY_EXISTS ]
     * 공통 - emailDTO 의 형식이 맞지 않는 경우 (정규식 사용)- "이메일 형식이 유효하지 않습니다." [ INVALID_REGISTER_EMAIL ]
     * <p>
     * 1. (토큰없이 이메일 데이터로만 API 호출) 이메일 객체가 없을 때 - 이메일 객체 생성
     * 2. (토큰없이 이메일 데이터로만 API 호출) 이메일 객체가 존재 할 때 -> 인증코드 재발송 (emailToken 변경)
     * <p>
     * 3. (토큰과 이메일 데이터로 API 호출) 이메일 객체가 없을 때 - "이메일 인증시간이 지났습니다, 다시 인증번호를 발급해주세요" [ EMAIL_ALREADY_EXPIRED ]
     * 4. (토큰과 이메일 데이터로 API 호출) 이메일 객체가 있지만 토큰이 다를 경우 - "이메일 인증번호가 일치하지 않습니다"  [ INVALID_EMAIL_TOKEN ]
     * 5. (토큰과 이메일 데이터로 API 호출) 이메일 객체가 있지만 10분이 지났을 때 - "이메일 인증시간이 지났습니다, 다시 인증번호를 발급해주세요" [ EMAIL_ALREADY_EXPIRED ]
     * 6. (토큰과 이메일 데이터로 API 호출) 이메일 객체의 값과 토큰이 일치할 경우 - HttpResponseStatus 200 : OK
     * <p>
     * 7. 이메일 객체 생성 후 10분이 지났을 때 X -> 정각 2시간 마다 객체 삭제
     */
    @Transactional
    public ResponseCode checkEmail(EmailDTO emailDTO) throws CustomException {

        String emailToken = RandomStringUtils.random(8, true, true);
        String receivedEmail = emailDTO.getEmail();
        String receivedToken = emailDTO.getEmailToken();
        Optional<Email> emailOpt = emailRepository.findByUserEmail(receivedEmail);
        Optional<Account> accountOpt = accountRepository.findByEmail(receivedEmail);

        if (accountOpt.isPresent()) {
            throw new CustomException(USER_ALREADY_EXISTS);
        }

        /* 이메일의 형식 검사 필요(정규식) */

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

    public void checkVerified(String email) throws CustomException {
        Email emailFound = emailRepository.findByUserEmail(email).orElseThrow(
                () -> new CustomException(EMAIL_CHECK_NOT_FOUND)
        );
        if (!emailFound.isEmailVerified()) {
            throw new CustomException(UNVERIFIED_EMAIL);
        }
    }

    @Transactional
    public void clearanceEmail() {
        emailRepository.deleteAllByExpireDateBefore(LocalDateTime.now());
    }

    public void sendEmail(String email, String emailToken) {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(email);
        simpleMessage.setSubject("marketClip 이메일 인증");
        simpleMessage.setText("이메일 인증번호=" + emailToken);
        javaMailSender.send(simpleMessage);
    }

    public void findEmail(String email) throws CustomException{
        if(accountRepository.findByEmail(email).isEmpty()){
            throw new CustomException(EMAIL_NOT_FOUND);
        }
    }

}
