package com.example.demo.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat") // ✅ 프론트에서 이 경로로 접근하게 됨
@RequiredArgsConstructor
public class ChatRoomController {

    @Autowired
    private final chatMessageMapper chatMapper;
    private final ChatService chatService;

    @PostMapping("/createRoom")
    public ResponseEntity<Long> createRoom(@RequestBody ChatRoomRequest request) {
        Long roomId = chatService.createRoom(request.getMemberIds(), request.isGroup());
        return ResponseEntity.ok(roomId);
    }

    @PostMapping("/rooms")
    public Map<String,Object> getMyChatRooms(@RequestBody ChatRoomDto chatRoomDto) {
        String userId = chatRoomDto.getUserId();
        List<ChatRoomDto> dto = new ArrayList<>();
        Map returnMap = new HashMap();
        returnMap.put("data" , chatMapper.selectRoomsByUser(userId));
        return returnMap;
    }
}
