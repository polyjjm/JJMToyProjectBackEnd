package com.example.demo.chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface chatMessageMapper {
    void insertMessage(chatMessage message);
    List<chatMessage> selectMessagesByRoomId(@Param("roomId") Long roomId);

    boolean isMemberOfRoom(@Param("userId") String userId, @Param("roomId") Long roomId);

    void insertChatRoom(@Param("isGroup") boolean isGroup);
    Long getLastInsertId();

    void insertChatRoomMember(@Param("roomId") Long roomId, @Param("userId") String userId);

    Long findExistingOneToOneRoom(@Param("user1") String user1, @Param("user2") String user2);

    List<ChatRoomDto> selectRoomsByUser(@Param("userId") String userId);

    void markMessagesAsRead(@Param("roomId") Long roomId, @Param("userId") String userId);

    // 1. '게스트 그룹채팅방'이 존재하는지 찾기
    Long findGuestGroupRoomId();

    // 2. 새로운 그룹 채팅방 생성
    void insertGroupRoom();




}
