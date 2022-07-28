package com.seven.marketclip.notifications.service;

import com.seven.marketclip.notifications.domain.Notifications;
import com.seven.marketclip.notifications.enums.NotificationsTypeEnum;
import com.seven.marketclip.notifications.repository.EmitterRepository;
import com.seven.marketclip.notifications.repository.EmitterRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    public SseEmitter subscribe(String userId, String lastEventId) {
        System.out.println("라스트 아이디 :" + lastEventId);
        // 1
        String id = userId + "_" + System.currentTimeMillis();
        System.out.println(id);
        // 2
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 3
        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [userId=" + id + "]");

        // 4
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        // 미수신한 알람들을 재전송해주는게 아니라
        // 클라이언트와 연결이 되어 있지만 클라이언트가
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }


    public void send(Long accountId, String nickName, String content) {  //accounId를 account 객체로 받기
        Notifications notifications = createNotification(accountId, nickName, content);
        String id = String.valueOf(accountId);

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notifications);
                    // 데이터 전송
                    sendToClient(emitter, key, notifications);
                }
        );
    }

    private Notifications createNotification(Long accountId, String nickName,String message) {
        //여기서 알림 메세지

        return Notifications.builder()
                .receiverId(accountId)
                .type(NotificationsTypeEnum.WISHLIST)
                .message(message)
                .referenceUrl("/sell-check/" + accountId)
                .checkRead(false)
                .build();
    }
    // 3
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결 오류!");
        }
    }
}