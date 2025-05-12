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
}
