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

    // Advances one member's read pointer to "everything currently in the room" - called when
    // that member opens/views the room (see ChatRoomController.markRead). This single UPDATE
    // is what drives both the unread badge (selectRoomsByUser) and the read-receipt check
    // (isReadByOthers) - see the 2026-08-16 migration for why this replaced the old
    // per-message is_read boolean.
    void updateLastReadMessageId(@Param("roomId") Long roomId, @Param("userId") String userId);

    // Sum of unread counts across every room the user is in - drives the header bell badge.
    Long selectTotalUnreadCount(@Param("userId") String userId);

    // The slowest-reading OTHER member's last_read_message_id in this room (MIN across everyone
    // except the caller) - null if no other member has read anything yet. Fetched ONCE per room
    // view rather than once per message: the frontend marks one of my own sent messages "읽음"
    // when that message's id <= this value. In a 1:1 room this is exactly "did the other person
    // read it"; in a group room it's "has the whole room caught up to it", which is the
    // simplest group read-receipt semantic and close enough to most chat apps' behavior for
    // personal use.
    Long selectOthersMinLastRead(@Param("roomId") Long roomId, @Param("userId") String userId);

    // Header (avatar/name) info for the popup conversation window - see selectRoomsByUser's
    // javadoc for the opponentName/memberCount semantics, this is the single-room version.
    ChatRoomDto selectRoomInfo(@Param("roomId") Long roomId, @Param("userId") String userId);

}
