package com.seven.marketclip.account.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.account.AccountTypeEnum;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.validation.AccountVerification;
import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.email.EmailService;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.image.domain.AccountImage;
import com.seven.marketclip.image.service.ImageService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class AccountService {

    private final EmailService emailService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountVerification accountVerification;
    private final FileCloudService fileCloudService;
    private final ImageService imageService;

    public AccountService(EmailService emailService, AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AccountVerification accountVerification, S3CloudServiceImpl s3CloudService, ImageService imageService) {
        this.emailService = emailService;
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountVerification = accountVerification;
        this.fileCloudService = s3CloudService;
        this.imageService = imageService;
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
        imageService.saveAccountImage("default", account);

        return SUCCESS;
    }

    // 유저 프로필 S3 업로드
    @Transactional
    public DataResponseCode addS3UserImage(MultipartFile multipartFile, Long accountId) throws CustomException {
        String profileUrl = fileCloudService.uploadFile(multipartFile);
        Map<String, Object> idUrlMap = new HashMap<>();
        idUrlMap.put("id", accountId);
        idUrlMap.put("url", profileUrl);

        AccountImage accountImage = imageService.findAccountImage(accountId);
        if (accountImage.getImageUrl().equals("default")) {
            accountImage.updateUrl(profileUrl);
        } else {
            fileCloudService.deleteFile(accountImage.getImageUrl());
            accountImage.updateUrl(profileUrl);
        }

        return new DataResponseCode(SUCCESS, idUrlMap);
    }

    //프로필 이미지 삭제
    @Transactional
    public ResponseCode profileImgDelete(Long accountId) throws CustomException {
        AccountImage accountImage = imageService.findAccountImage(accountId);

        if (!accountImage.getImageUrl().equals("default")) {
            imageService.deleteAccountImage(accountId);
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

    //프로필 비밀번호 찾기
    @Transactional
    public ResponseCode findPassword(String email) {
//        emailService.checkEmail();
        return SUCCESS;
    }

}
