package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.dto.ChatRoomReq;
import com.seven.marketclip.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")   //채팅방 만들기 API 1번
    public void chatRoomSave(@RequestBody ChatRoomReq req) {
        chatRoomService.saveChatRoom(req);
    }
    @DeleteMapping ("/room")
    public void chatRoomRemove(@RequestParam List<Long> chatRoomId){
        chatRoomService.removeChatRoom(chatRoomId);
    }

}