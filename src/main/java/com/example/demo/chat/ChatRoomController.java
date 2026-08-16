package com.example.demo.chat;

import com.example.demo.common.commonServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final commonServiceImpl commonServiceImpl;

    // Returns {data: roomId} (not a raw Long) so it's consistent with the frontend's post()
    // helper, which unwraps a top-level "data" key from every response (see common.tsx) - the
    // raw-Long shape this used to return worked only because the old caller (chatMain.tsx)
    // never actually used the returned id; ChatPanel.tsx's create-room flow needs it to
    // immediately open the new room's popup.
    @PostMapping("/createRoom")
    public Map<String, Object> createRoom(@RequestBody ChatRoomRequest request) {
        Long roomId = chatService.createRoom(request.getMemberIds(), request.getIsGroup());
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", roomId);
        return returnMap;
    }

    @PostMapping("/rooms")
    public Map<String,Object> getMyChatRooms(@RequestBody ChatRoomDto chatRoomDto) {
        String userId = chatRoomDto.getUserId();
        List<ChatRoomDto> dto = new ArrayList<>();
        Map returnMap = new HashMap();
        returnMap.put("data" , chatMapper.selectRoomsByUser(userId));
        return returnMap;
    }
    @PostMapping("/joinGuestRoom")
    public Map<String,Object> joinGuestRoom(@RequestBody Map<String, String> request) {
        String guestId = request.get("guestId");
        Long roomId = chatService.joinOrCreateGuestGroupRoom(guestId);

        Map returnMap = new HashMap();
        returnMap.put("data" , roomId);
        return returnMap;
    }

    // Header bell badge - total unread across every room the user is in.
    @GetMapping("/unreadCount/{userId}")
    public Map<String, Object> getUnreadCount(@PathVariable String userId) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", chatMapper.selectTotalUnreadCount(userId));
        return returnMap;
    }

    // Image chat messages reuse the exact same upload (+ automatic thumbnail generation) as
    // board images - see commonServiceImpl.ckEditorUpload. The frontend uploads the file here
    // first, then publishes a normal STOMP chat.send message with messageType=IMAGE and this
    // URL as attachmentUrl (see chatRoom.tsx).
    @PostMapping("/uploadImage")
    public Map<String, Object> uploadImage(MultipartFile[] upload) throws Exception {
        List<String> urlList = commonServiceImpl.ckEditorUpload(upload);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", urlList.isEmpty() ? null : urlList.get(0));
        return returnMap;
    }
}
