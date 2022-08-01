package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.dto.ChatRoomGoods;
import com.seven.marketclip.chat.dto.RoomMake;
import com.seven.marketclip.chat.dto.*;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/api/room")   //채팅방 만들기 API 1번
    public void chatRoomSave(@RequestBody RoomMake room, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.saveChatRoom(room, userDetails.getId());
    }
    @DeleteMapping ("/api/room")
    public void chatRoomRemove(@RequestBody ChatMessageInfo room, @AuthenticationPrincipal UserDetailsImpl userDetails){
        chatRoomService.removeChatRoom(room.getChatRoomId(), userDetails.getId());
    }

    @GetMapping("/api/chat-rooms")   //메시지 도착했을때 이 API 호출해주세요 로그인 아이디 넣어주기
    @Cacheable(key = "#userDetails.id", cacheNames = "chatRoomCache")
    public List<ChatRoomGoods> chatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.findChatRooms(userDetails.getId());
    }
}