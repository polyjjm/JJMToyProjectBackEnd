package com.example.demo.chat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/api/chat/history/{roomId}")
    public ResponseEntity<List<chatMessage>> getHistory(@PathVariable Long roomId) {
        List<chatMessage> list = new ArrayList<>();
        list  = messageMapper.selectMessagesByRoomId(roomId);
        return ResponseEntity.ok(list);
    }
}
