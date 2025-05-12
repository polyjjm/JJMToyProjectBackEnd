package com.example.demo.chat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class chatController {
    private final chatMessageMapper messageMapper;

    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/topic/{roomId}")
    public chatMessage sendMessage(@DestinationVariable Long roomId, chatMessage message) {
        message.setRoomId(roomId);
        messageMapper.insertMessage(message);
        return message;
    }

//    @GetMapping("/api/chat/history/{roomId}")
//    public ResponseEntity<List<chatMessage>> getHistory(@PathVariable Long roomId) {
//        List<chatMessage> list = new ArrayList<>();
//        list  = messageMapper.selectMessagesByRoomId(roomId);
//        return ResponseEntity.ok(list);
//    }

    @GetMapping("/api/chat/history/{roomId}/{userId}")
    public ResponseEntity<?> getHistory(@PathVariable Long roomId, @PathVariable String userId) {
        if (!messageMapper.isMemberOfRoom(userId, roomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }
        return ResponseEntity.ok(messageMapper.selectMessagesByRoomId(roomId));
    }

}
