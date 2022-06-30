package com.seven.marketclip.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final  AccountRepository accountRepository;

    public ResponseEntity<?> nicknameValidation(String nickname){
        if(!accountRepository.existsByNickname(nickname)){
            return ResponseEntity.badRequest().body("이미 존재하는 닉네임 입니다.");
        }
        return ResponseEntity.ok().body(null);
    }

}
