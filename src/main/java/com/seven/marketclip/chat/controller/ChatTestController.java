package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.dto.ChatRoomGoods;
import com.seven.marketclip.chat.dto.ChatRoomId;
import com.seven.marketclip.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatTestController {       //테스트 후 삭제 클래스
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")   //채팅방 만들기 API 1번
    public void chatRoomSave(@RequestBody ChatRoomId req) {
        chatRoomService.saveChatRoom(req.getBuyerId(), req.getGoodsId());
    }
    @DeleteMapping("/room")
    public void chatRoomRemove(@RequestParam List<Long> chatRoomId){
        chatRoomService.removeChatRoom(chatRoomId);
    }

    @GetMapping("/chat-rooms/{id}")   //메시지 도착했을때 이 API 호출해주세요 로그인 아이디 넣어주기
    public List<ChatRoomGoods> chatRooms(@PathVariable Long id) {
        return chatRoomService.findChatRooms(id);
    }

}
