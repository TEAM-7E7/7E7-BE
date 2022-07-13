package com.seven.marketclip.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {

    Optional<Email> findByUserEmail(String email);
    void deleteAllByExpireDateBefore(LocalDateTime localDateTime);

}
