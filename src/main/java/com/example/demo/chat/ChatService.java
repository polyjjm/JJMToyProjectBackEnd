package com.example.demo.chat;

import com.example.demo.board.boardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ChatService  {
    @Autowired
    private final chatMessageMapper chatMapper;

    private final String ADMIN_ID = "3971393861"; // 관리자 고정 ID
    public Long createRoom(List<String> memberIds, boolean isGroup) {
        // 1:1 중복 방 검사
        if (!isGroup && memberIds.size() == 2) {
            Long existingRoomId = chatMapper.findExistingOneToOneRoom(memberIds.get(0), memberIds.get(1));
            if (existingRoomId != null) {
                return existingRoomId;
            }
        }

        // 채팅방 생성
        chatMapper.insertChatRoom(isGroup);
        Long roomId = chatMapper.getLastInsertId();

        // 참여자 등록
        for (String userId : memberIds) {
            chatMapper.insertChatRoomMember(roomId, userId);  // 👈 여기가 핵심
        }

        return roomId;
    }

    public Long joinOrCreateGuestGroupRoom(String guestId) {
        Long roomId = chatMapper.findGuestGroupRoomId();

        if (roomId == null) {
            chatMapper.insertGroupRoom(); // INSERT INTO chat_room (is_group) VALUES (true)
            roomId = chatMapper.getLastInsertId();

            chatMapper.insertChatRoomMember(roomId, ADMIN_ID); // 관리자 추가
        }

        if (!chatMapper.isMemberOfRoom(guestId, roomId)) {
            chatMapper.insertChatRoomMember(roomId, guestId);
        }

        return roomId;
    }
}
