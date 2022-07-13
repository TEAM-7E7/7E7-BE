package com.seven.marketclip.account.validation;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.seven.marketclip.exception.ResponseCode.*;

@RequiredArgsConstructor
@Component
public class AccountVerification {

    //익셉션 핸들러들 각각 언제 발동 되는가?
    private final AccountRepository accountRepository;

    //유저 아이디 중복 체크
    public Account checkVerificationId(Long id) throws CustomException{
        return accountRepository.findById(id).orElseThrow(
                ()-> new CustomException(VERIFICATION_ID)
        );
    }

    //이미지 중복 체크
    public void checkVerificationEmail(String email) throws CustomException {
        if(!accountRepository.existsByEmail(email)){
            throw new CustomException(VERIFICATION_EMAIL);
        }
    }

    //닉네임 중복 체크
    public void checkVerificationNickname(String email) throws CustomException {
        if(!accountRepository.existsByNickname(email)){
            throw new CustomException(VERIFICATION_NICKNAME);
        }
    }


}