package com.seven.marketclip.image.repository;

import com.seven.marketclip.image.domain.AccountImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountImageRepository extends JpaRepository<AccountImage, Long> {
    Optional<AccountImage> findByAccountId(Long accountId);
}
