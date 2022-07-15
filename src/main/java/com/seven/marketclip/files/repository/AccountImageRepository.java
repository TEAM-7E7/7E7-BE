package com.seven.marketclip.files.repository;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.files.domain.AccountImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountImageRepository extends JpaRepository<AccountImage, Long> {
    Optional<AccountImage> findByAccountId(Long accountId);
    Optional<AccountImage> findByAccount(Account account);
}
