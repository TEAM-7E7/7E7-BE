package com.seven.marketclip.notifications.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "receiver_id")
    private Long receiverId;

    private String message;

    @Column(name = "read_or_not")
    private boolean readOrNot;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private NotificationsTypeEnum type;

    @Column(name = "reference_id")
    private Long referenceId;


}
