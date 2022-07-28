package com.seven.marketclip.notifications.domain;

import com.seven.marketclip.notifications.enums.NotificationsTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "receiver_id")
    private Long receiverId;
    private String message;                 //content
    private boolean checkRead;              // 읽음 여부 확인

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private NotificationsTypeEnum type;     //타입 별로 제목
    private String referenceUrl;

    @Builder
    public Notifications(Long id, Long receiverId,NotificationsTypeEnum type, String message, String referenceUrl, Boolean checkRead) {
        this.id = id;
        this.receiverId = receiverId;
        this.type = type;
        this.message = message;
        this.referenceUrl = referenceUrl;
        this.checkRead = checkRead;
    }

}
