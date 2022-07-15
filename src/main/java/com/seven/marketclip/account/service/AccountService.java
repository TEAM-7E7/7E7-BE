package com.seven.marketclip.account.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.account.AccountTypeEnum;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.validation.AccountVerification;
import com.seven.marketclip.cloudServer.service.FileCloudService;
import com.seven.marketclip.cloudServer.service.S3CloudServiceImpl;
import com.seven.marketclip.email.EmailService;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.files.domain.AccountImage;
import com.seven.marketclip.files.service.FileService;
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
    private final AccountVerification accountVerification;
    private final FileCloudService fileCloudService;
    private final FileService fileService;

    public AccountService(EmailService emailService, AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AccountVerification accountVerification, S3CloudServiceImpl s3CloudService, FileService fileService, S3CloudServiceImpl fileCloudService) {
        this.emailService = emailService;
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountVerification = accountVerification;
        this.fileCloudService = fileCloudService;
        this.fileService = fileService;
    }

    //닉네임 증복체크
    public ResponseCode checkNickname(String nickname) throws CustomException {
        Optional<Account> accountOpt = accountRepository.findByNickname(nickname);
        if (accountOpt.isPresent()) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
        return SUCCESS;
    }

    // marketClip 회원가입
    @Transactional
    public ResponseCode addUser(AccountReqDTO accountReqDTO) throws CustomException {
        String encodedPassword = bCryptPasswordEncoder.encode(accountReqDTO.getPassword());

        Optional<Account> accountOpt = accountRepository.findByEmail(accountReqDTO.getEmail());
        if (accountOpt.isPresent()) {
            throw new CustomException(USER_ALREADY_EXISTS);
        }

        Account account = Account.builder()
                .email(accountReqDTO.getEmail())
                .nickname(accountReqDTO.getNickname())
                .password(encodedPassword)
                .role(AccountRoleEnum.USER)
                .type(AccountTypeEnum.MARKETCLIP)
                .build();

        emailService.checkVerified(accountReqDTO.getEmail());

        accountRepository.save(account);
        fileService.saveAccountImage("default",account);

        return SUCCESS;
    }

    //프로필 이미지 수정
    @Transactional
    public ResponseCode updateProfileImg(Long accountId, String imgUrl) throws CustomException {
        Account account = accountVerification.checkVerificationId(accountId);        // todo : account에 대한 검증을 또 해야하나...?
        AccountImage accountImage = fileService.findAccountImage(accountId);

        if (accountImage.getImageUrl().equals("default")) {
            accountImage.updateUrl(imgUrl);
        } else {
            fileService.saveAccountImage(imgUrl, account);
            fileCloudService.deleteFile(accountImage.getImageUrl());
        }
        return SUCCESS;
    }

    //프로필 이미지 삭제
    @Transactional
    public ResponseCode profileImgDelete(Long accountId) throws CustomException {
        AccountImage accountImage = fileService.findAccountImage(accountId);

        if (! accountImage.getImageUrl().equals("default")) {
            fileService.deleteAccountImage(accountId);
            fileCloudService.deleteFile(accountImage.getImageUrl());
        }
        return SUCCESS;
    }

    //프로필 닉네임 수정
    @Transactional
    public ResponseCode updateNickname(Long id, String nickname) {
        Account account = accountVerification.checkVerificationId(id);
        account.changeNickname(nickname);

        //여기에 JWT 재발급? -> 다른 수정들도...

        return NICKNAME_UPDATE_SUCCESS;
    }

    //프로필 비밀번호 수정
    @Transactional
    public ResponseCode updatePassword(Long id, String password) {
        Account account = accountVerification.checkVerificationId(id);
        account.changePassword(password);
        account.encodePassword(bCryptPasswordEncoder);
        return PASSWORD_VALIDATION_SUCCESS;
    }

}
