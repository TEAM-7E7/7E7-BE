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
    public Account checkAccount(Long id) throws CustomException {
        return accountRepository.findById(id).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    //이메일 중복 체크
    public void checkEmail(String email) throws CustomException {
        if (accountRepository.existsByEmail(email)) {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        }
    }

    //닉네임 중복 체크
    public void checkNickname(String nickname) throws CustomException {
        if (accountRepository.existsByNickname(nickname)) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
    }


}