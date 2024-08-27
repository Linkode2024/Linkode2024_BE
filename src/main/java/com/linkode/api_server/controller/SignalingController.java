package com.linkode.api_server.controller;

import com.linkode.api_server.service.MemberStudyroomService;
import com.linkode.api_server.service.WebSocketSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Controller
@RequiredArgsConstructor
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionService sessionService;
    private final MemberStudyroomService memberStudyroomService;

    @MessageMapping("/studyroom/{studyroomId}/sendMessage")
    @SendTo("/topic/studyroom/{studyroomId}")
    public String handleStudyroomMessage(@DestinationVariable String studyroomId,
                                         SimpMessageHeaderAccessor headerAccessor,
                                         @Payload Map<String, Object> payload) {
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        String type = (String) payload.get("type");
        if (type == null) {
            return "Invalid message type";
        }

        switch (type) {
            case "join":
                return "User " + payload.get("userId") + " has joined the room.";

            case "leave":
                return "User " + payload.get("userId") + " has left the room.";

            case "message":
                return "User " + memberId + ": " + payload.get("content");

            default:
                return "Unknown message type";
        }
    }

    /** 이상합니다.. 세션을 어떤거 기반으로 추출해야할지 혼동... */
    @MessageMapping("/studyroom/{studyroomId}/callUser")
    public void callUser(
            @DestinationVariable String studyroomId,
            SimpMessageHeaderAccessor headerAccessor,
            @Payload String targetUserId) {

        // 호출하는 사용자의 ID 가져오기
        Long callerId = (Long) headerAccessor.getSessionAttributes().get("memberId");

        // 특정 사용자의 세션을 가져와서 메시지를 보냅니다.
        WebSocketSession targetSession = sessionService.getSession(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            messagingTemplate.convertAndSendToUser(targetUserId, "/queue/private",
                    "Call from user " + callerId + " in studyroom " + studyroomId);
        }
    }

}
