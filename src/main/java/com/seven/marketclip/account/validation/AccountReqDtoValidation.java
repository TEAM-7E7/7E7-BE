package com.seven.marketclip.account.validation;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.dto.AccountReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AccountReqDtoValidation implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountReqDTO.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        AccountReqDTO dto = (AccountReqDTO)object;
        Optional<Account> accountOptEmail = accountRepository.findByEmail(dto.getEmail());
        Optional<Account> accountOptNickname = accountRepository.findByNickname(dto.getEmail());
        if (accountOptEmail.isPresent()) {
            errors.rejectValue("email", "invalid.email", new Object[]{dto.getEmail()}, "이미 사용중인 이메일입니다.");
        }

        if (accountOptNickname.isPresent()) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{dto.getEmail()}, "이미 사용중인 닉네임입니다.");
        }
    }

}
