package com.example.demo.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomDto {
    private Long room_id;
    @JsonProperty("is_group")
    private boolean is_group;
    private String room_name;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    // TEXT/IMAGE - lets the room list show "(이미지)" instead of the (null) message text for
    // an image last-message, per the chat panel mockup.
    private String messageType;
    private String lastMessageSenderId;
    // Unread count for THIS room, for THIS request's userId - computed against that member's
    // chat_room_member.last_read_message_id (see chatMessageMapper.xml selectRoomsByUser).
    private Long unreadCount;
    // 1:1 rooms only - the other member's display name (member_user.user_name, falling back to
    // their raw user_id for guests, who aren't in member_user at all - see NicknameInputPage.tsx).
    // null for group rooms.
    private String opponentName;
    // Group rooms only - used for a "그룹채팅 (N명)" fallback label when room_name isn't set
    // (createRoom doesn't ask for a name today).
    private Integer memberCount;
    private String userId;
}
