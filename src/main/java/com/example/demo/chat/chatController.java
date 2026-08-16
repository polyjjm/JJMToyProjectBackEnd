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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class chatController {
    private final chatMessageMapper messageMapper;
    private final ChatService chatService;

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
        // Opening the room IS "reading" it - advance this member's pointer to everything
        // currently in the room (see chatMapper.xml updateLastReadMessageId).
        messageMapper.updateLastReadMessageId(roomId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("messages", messageMapper.selectMessagesByRoomId(roomId));
        // Read-receipt cutoff for MY OWN sent messages: id <= othersReadUpTo means every other
        // member of the room has caught up to that message. null = no other member has read
        // anything yet.
        response.put("othersReadUpTo", messageMapper.selectOthersMinLastRead(roomId, userId));
        return ResponseEntity.ok(response);
    }

    // Header (avatar/name) for the popup conversation window - fetched once on mount.
    @GetMapping("/api/chat/roomInfo/{roomId}/{userId}")
    public ResponseEntity<?> getRoomInfo(@PathVariable Long roomId, @PathVariable String userId) {
        if (!messageMapper.isMemberOfRoom(userId, roomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }
        return ResponseEntity.ok(messageMapper.selectRoomInfo(roomId, userId));
    }

    // Lightweight poll target for the popup window: while a conversation is open, the "읽음"
    // indicator under my own messages needs to update as the other side reads, without a
    // dedicated per-member WebSocket read-status topic (see chatRoom.tsx's polling interval -
    // simple and cheap enough at personal-site scale, matching "reuse the existing STOMP setup
    // rather than replacing it").
    @GetMapping("/api/chat/readStatus/{roomId}/{userId}")
    public ResponseEntity<?> getReadStatus(@PathVariable Long roomId, @PathVariable String userId) {
        if (!messageMapper.isMemberOfRoom(userId, roomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("othersReadUpTo", messageMapper.selectOthersMinLastRead(roomId, userId));
        return ResponseEntity.ok(response);
    }

}
