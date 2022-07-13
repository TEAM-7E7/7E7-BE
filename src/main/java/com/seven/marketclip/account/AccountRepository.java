package com.seven.marketclip.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmailAndNickname(String email, String nickname);



    Optional<Account> findByEmail(String email);
    Optional<Account> findByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    //리프레쉬 토큰
    boolean existsByRefreshToken(String token);

}