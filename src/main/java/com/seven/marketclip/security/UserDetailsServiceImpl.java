package com.seven.marketclip.security;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { //로그인 때 입력한 아이디
//        System.out.println("로그인 필터 4");
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + email));


//        System.out.println("우리가 쓴 패스워드 : " +account.getPassword());
//        System.out.println("로그인 필터 5");


//        System.out.println("유저 디테일스 서비스 : " + account.getProfileImgUrl().getImageUrl());

        return UserDetailsImpl.builder()
                .id(account.getId())
                .password(account.getPassword())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .profileImgUrl(account.getProfileImgUrl().getImageUrl())
                .role(account.getRole())
                .build();
    }
}