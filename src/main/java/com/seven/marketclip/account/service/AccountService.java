package com.seven.marketclip.account.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.account.repository.AccountRepository;
import com.seven.marketclip.account.repository.AccountRoleEnum;
import com.seven.marketclip.account.repository.AccountTypeEnum;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.validation.AccountVerification;
import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.email.EmailService;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.image.domain.AccountImage;
import com.seven.marketclip.image.domain.GoodsImage;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.seven.marketclip.exception.ResponseCode.*;
import static com.seven.marketclip.image.service.ImageService.DEFAULT_PROFILE_IMAGE;
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

    //닉네임 증복체크
    public ResponseCode checkNickname(String nickname) throws CustomException {
        accountVerification.checkNickname(nickname);
        return SUCCESS;
    }

    // marketClip 회원가입
    @Transactional
    public ResponseCode addUser(AccountReqDTO accountReqDTO) throws CustomException {
        String encodedPassword = bCryptPasswordEncoder.encode(accountReqDTO.getPassword());

        accountVerification.checkEmail(accountReqDTO.getEmail());
        accountVerification.checkNickname(accountReqDTO.getNickname());
        emailService.checkVerified(accountReqDTO.getEmail());

        Account account = Account.builder()
                .email(accountReqDTO.getEmail())
                .nickname(accountReqDTO.getNickname())
                .password(encodedPassword)
                .role(AccountRoleEnum.USER)
                .type(AccountTypeEnum.MARKETCLIP)
                .build();

        accountRepository.save(account);
        imageService.saveAccountImage(DEFAULT_PROFILE_IMAGE, account);
        emailService.deleteEmailVerified(accountReqDTO.getEmail());

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
        if (!accountImage.getImageUrl().equals(DEFAULT_PROFILE_IMAGE)) {
            fileCloudService.deleteFile(accountImage.getImageUrl());
        }
        accountImage.updateUrl(profileUrl);
        return new DataResponseCode(SUCCESS, idUrlMap);
    }

    //프로필 이미지 삭제
    @Transactional
    public ResponseCode profileImgDelete(Long accountId) throws CustomException {
        AccountImage accountImage = imageService.findAccountImage(accountId);

        if (accountImage.getImageUrl().equals(DEFAULT_PROFILE_IMAGE)) {
            throw new CustomException(ACCOUNT_IMAGE_NOT_FOUND);
        }
        fileCloudService.deleteFile(accountImage.getImageUrl());
        imageService.deleteAccountImage(accountId);

        return SUCCESS;
    }

    // 닉네임 변경
    @Transactional
    public ResponseCode updateNickname(Long id, String nickname) {
        Account account = accountVerification.checkAccount(id);
        accountVerification.checkNickname(nickname);
        account.changeNickname(nickname);
        return SUCCESS;
    }

    // 비밀번호 변경 (이메일 확인 이후)
    @Transactional
    public ResponseCode changePassword(String email, String password) {
        Account account = accountRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(EMAIL_NOT_FOUND)
        );
        emailService.checkVerified(email);
        account.changePassword(password);
        account.encodePassword(bCryptPasswordEncoder);
        emailService.deleteEmailVerified(email);
        return SUCCESS;
    }

    // 비밀번호 변경 (로그인 상태)
    @Transactional
    public ResponseCode updatePassword(UserDetailsImpl userDetails, String password) {
        Account account = accountVerification.checkAccount(userDetails.getId());
        account.changePassword(password);
        account.encodePassword(bCryptPasswordEncoder);
        return SUCCESS;
    }

    // 회원 탈퇴
    @Transactional
    public ResponseCode deleteUser(Long accountId) {
        Account account = accountVerification.checkAccount(accountId);
        List<Goods> goodsList = account.getGoodsList();
        AccountImage accountImage = account.getProfileImgUrl();

        if (goodsList.isEmpty() || accountImage.getImageUrl().equals("default")) {
            deleteByAccountId(accountId);
        } else {
            List<List<GoodsImage>> cascadeUrlsList = goodsList.stream().map(Goods::getGoodsImages).collect(Collectors.toList());
            List<GoodsImage> cascadeUrls = cascadeUrlsList.stream().flatMap(List::stream).collect(Collectors.toList());

            try {
                deleteByAccountId(accountId);
            } catch (Exception e){
                return SUCCESS;
            }

            fileCloudService.deleteFile(accountImage.getImageUrl());
            for (GoodsImage goodsImage : cascadeUrls) {
                fileCloudService.deleteFile(goodsImage.getImageUrl());
            }
        }
        return SUCCESS;
    }

    @Transactional
    public void deleteByAccountId(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    //리프레쉬 토큰 재발
    @Transactional
    public ResponseCode reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refresh = request.getHeader("X-REFRESH-TOKEN");
        if (refresh == null) {
            return REFRESH_TOKEN_NO_HEADER;
        }

        //TODO Decoder에 있는거 같은 함수로 빼기
        //올바른 토큰인지 확인
        refresh = headerTokenExtractor.extract(refresh, request, response);


        //만료된 토큰인지 확인 -> JWT필터에서도 해줘야함.
//        Long id = jwtDecoder.decodeUserId(refresh); //여기 안에서 만료됐는지 확인.
        DecodedJWT jwt = null;
        Long id = null;

        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        JWTVerifier verifier = JWT
                .require(algorithm)
                .build();

        try {
            jwt = verifier.verify(refresh);
        } catch (Exception e) {
            return REFRESH_TOKEN_VERIFY;
        }


        Date expiredDate = jwt
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            return REFRESH_TOKEN_EXPIRED;
        }
        id = jwt
                .getClaim(CLAIM_USER_ID)
                .asLong();

        Optional<Account> accounts = accountRepository.findById(id);
        if (accounts.isEmpty()) {
            return REFRESH_TOKEN_ID_NOT_EXIST;
        }
        Account account = accounts.get();

        if (!refresh.equals(account.getRefreshToken())) {
            return REFRESH_TOKEN_NOT_EXIST_DB;
        }

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(account.getId())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .profileImgUrl(account.getProfileImgUrl().getImageUrl())
                .type(account.getType())
                .role(account.getRole())
                .build();


        //JWT토큰과 Refresh 토큰 재발급
        final String reissuanceJWT = JwtTokenUtils.generateJwtToken(userDetails);
        final String reissuanceRefreshToken = JwtTokenUtils.generateRefreshToken(userDetails);
        account.changeRefreshToken(reissuanceRefreshToken);

        response.addHeader(FormLoginSuccessHandler.JWT_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceJWT);
        response.addHeader(FormLoginSuccessHandler.REFRESH_HEADER, FormLoginSuccessHandler.TOKEN_TYPE + " " + reissuanceRefreshToken);

        return SUCCESS;
    }

}
