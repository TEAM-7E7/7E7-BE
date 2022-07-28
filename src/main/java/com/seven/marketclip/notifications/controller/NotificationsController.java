package com.seven.marketclip.notifications.controller;

import com.seven.marketclip.notifications.dto.TestNotiDto;
import com.seven.marketclip.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NotificationsController {
    private final NotificationService notificationService;

    /**
     * @title 로그인 한 유저 sse 연결
     */
    @GetMapping(value = "/subscribe/{id}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable String id,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(id, lastEventId);
    }
    @PostMapping("/notifications-test")
    public void sendReview(@RequestParam String content, @RequestBody TestNotiDto test){
        notificationService.send(test.getAccountId(), test.getNickName(), test.getMessage());
    }
}
