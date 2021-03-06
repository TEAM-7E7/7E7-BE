package com.seven.marketclip.account.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
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
import com.seven.marketclip.security.FormLoginSuccessHandler;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.security.jwt.HeaderTokenExtractor;
import com.seven.marketclip.security.jwt.JwtTokenUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.seven.marketclip.exception.ResponseCode.*;
import static com.seven.marketclip.security.jwt.JwtTokenUtils.*;

@Service
public class AccountService {

    private final EmailService emailService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountVerification accountVerification;
    private final FileCloudService fileCloudService;
    private final ImageService imageService;
    private final HeaderTokenExtractor headerTokenExtractor;

    public AccountService(EmailService emailService, AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AccountVerification accountVerification, S3CloudServiceImpl s3CloudService, ImageService imageService, HeaderTokenExtractor headerTokenExtractor) {
        this.emailService = emailService;
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountVerification = accountVerification;
        this.fileCloudService = s3CloudService;
        this.imageService = imageService;
        this.headerTokenExtractor = headerTokenExtractor;
    }

    //????????? ????????????
    public ResponseCode checkNickname(String nickname) throws CustomException {
        Optional<Account> accountOpt = accountRepository.findByNickname(nickname);
        if (accountOpt.isPresent()) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
        return SUCCESS;
    }

    // marketClip ????????????
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

    // ?????? ????????? S3 ?????????
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

    //????????? ????????? ??????
    @Transactional
    public ResponseCode profileImgDelete(Long accountId) throws CustomException {
        AccountImage accountImage = imageService.findAccountImage(accountId);

        if (!accountImage.getImageUrl().equals("default")) {
            imageService.deleteAccountImage(accountId);
            fileCloudService.deleteFile(accountImage.getImageUrl());
        }
        return SUCCESS;
    }

    //????????? ????????? ??????
    @Transactional
    public ResponseCode updateNickname(Long id, String nickname) {
        Account account = accountVerification.checkVerificationId(id);
        account.changeNickname(nickname);

        //????????? JWT ?????????? -> ?????? ????????????...

        return NICKNAME_UPDATE_SUCCESS;
    }

    //????????? ???????????? ??????
    @Transactional
    public ResponseCode updatePassword(Long id, String password) {
        Account account = accountVerification.checkVerificationId(id);
        account.changePassword(password);
        account.encodePassword(bCryptPasswordEncoder);
        return PASSWORD_VALIDATION_SUCCESS;
    }

    //????????? ???????????? ??????
    @Transactional
    public ResponseCode findPassword(String email) {
//        emailService.checkEmail();
        return SUCCESS;
    }

    //???????????? ?????? ?????????
    public ResponseCode reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String refresh = request.getHeader("X-REFRESH-TOKEN");
        System.out.println("dasasffffffffffffffffffff");
        if (refresh == null) {
            return REFRESH_TOKEN_NO_HEADER;
        }

        //TODO Decoder??? ????????? ?????? ????????? ??????
        //????????? ???????????? ??????
        refresh = headerTokenExtractor.extract(refresh, request, response);
        System.out.println("???????????? ?????? ??????");
        System.out.println("???????????? ?????? jwt ?????? " + refresh);


        //????????? ???????????? ?????? -> JWT??????????????? ????????????.
//        Long id = jwtDecoder.decodeUserId(refresh); //?????? ????????? ??????????????? ??????.
        DecodedJWT jwt = null;
        Long id = null;

        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        JWTVerifier verifier = JWT
                .require(algorithm)
                .build();

        try{
            jwt = verifier.verify(refresh);
        }catch (Exception e){
            return REFRESH_TOKEN_VERIFY;
        }



        Date expiredDate = jwt
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();
        System.out.println("before");

        Date now = new Date();
        if (expiredDate.before(now)) {
            return REFRESH_TOKEN_EXPIRED;
        }
        System.out.println("after");
        id = jwt
                .getClaim(CLAIM_USER_ID)
                .asLong();

        Optional<Account> accounts = accountRepository.findById(id);
        if(accounts.isEmpty()){
            return REFRESH_TOKEN_ID_NOT_EXIST;
        }
        Account account = accounts.get();

        if (!refresh.equals(account.getRefreshToken())) {
            return REFRESH_TOKEN_NOT_EXIST_DB;
        }

        System.out.println("?????? ???????????? : " + account.getProfileImgUrl().getImageUrl());

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(account.getId())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .profileImgUrl(account.getProfileImgUrl().getImageUrl())
                .role(account.getRole())
                .build();


        //JWT????????? Refresh ?????? ?????????
        final String reissuanceJWT = JwtTokenUtils.generateJwtToken(userDetails);
        final String reissuanceRefreshToken = JwtTokenUtils.generateRefreshToken(userDetails);
        account.changeRefreshToken(reissuanceRefreshToken);

        response.addHeader(FormLoginSuccessHandler.JWT_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceJWT);
        response.addHeader(FormLoginSuccessHandler.REFRESH_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceRefreshToken);
        System.out.println("???????????? ?????? ???");

        return SUCCESS;
    }

}
