package com.seven.marketclip.account.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.account.AccountTypeEnum;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.email.EmailService;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.security.FormLoginSuccessHandler;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.security.jwt.JwtTokenUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class AccountService {

    private final EmailService emailService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountService(EmailService emailService, AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.emailService = emailService;
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //닉네임 증복체크
    public ResponseCode checkNickname(String nickname) {
        Optional<Account> accountOpt = accountRepository.findByNickname(nickname);
        if (accountOpt.isPresent()) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
        return NICKNAME_VALIDATION_SUCCESS;
    }

    // marketClip 회원가입
    @Transactional
    public ResponseCode addUser(AccountReqDTO accountReqDTO) throws CustomException {
        String encodedPassword = bCryptPasswordEncoder.encode(accountReqDTO.getPassword());

        Account account = Account.builder()
                .email(accountReqDTO.getEmail())
                .nickname(accountReqDTO.getNickname())
                .password(encodedPassword)
                .role(AccountRoleEnum.USER)
                .type(AccountTypeEnum.MARKETCLIP)
                .build();

        if(!emailService.checkVerified(accountReqDTO.getEmail())){
            throw new CustomException(UNVERIFIED_EMAIL);
        }

        accountRepository.save(account);

        return SIGNUP_SUCCESS;
    }

}
