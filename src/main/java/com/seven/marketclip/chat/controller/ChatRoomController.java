package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.dto.*;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
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
    public void chatRoomRemove(@RequestBody ChatRoomDeletes room){
        chatRoomService.removeChatRoom(room.getChatRoomId());
    }

//    @PostMapping("/api/find-room")          //방만들기 위한 true false
//    public boolean chatMessageList(@RequestBody ChatRoomReq roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return chatRoomService.findChatRoom(roomInfo.getGoodsId(), userDetails.getId(), roomInfo.getPartnerId());
//    }
    @GetMapping("/api/chat-rooms")   //메시지 도착했을때 이 API 호출해주세요 로그인 아이디 넣어주기
    public List<ChatRoomGoods> chatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.findChatRooms(userDetails.getId());
    }
}