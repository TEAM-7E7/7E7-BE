package com.week2.magazine.security;

import com.week2.magazine.account.Account;
import com.week2.magazine.account.AccountRepository;
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


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("로그인 필터 4");
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + email));
        System.out.println("우리가 쓴 패스워드 : " +account.getPassword());
        System.out.println("로그인 필터 5");
        return new UserDetailsImpl(account.getId(), account.getPassword(),account.getEmail(),account.getRole());
    }
}