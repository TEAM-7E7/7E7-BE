package com.seven.marketclip.notifications.repository;

import com.seven.marketclip.chat.domain.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<ChatMessages, Long> {

}
