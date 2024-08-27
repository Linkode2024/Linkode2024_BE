package com.linkode.api_server.controller;

import com.linkode.api_server.service.WebSocketSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

@Controller
@RequiredArgsConstructor
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionService sessionService;

    @MessageMapping("/studyroom/{studyroomId}/sendMessage")
    @SendTo("/topic/studyroom/{studyroomId}")
    public String handleStudyroomMessage(@DestinationVariable String studyroomId, SimpMessageHeaderAccessor headerAccessor, String message) {
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        if (memberId != null) {
            return "User " + memberId + ": " + message;
        } else {
            return "Anonymous: " + message;
        }    }

    @MessageMapping("/studyroom/{studyroomId}/callUser")
    public void callUser(
            @DestinationVariable String studyroomId,
            @Headers Map<String, Object> headers,
            @Payload String message) {
        String targetUserId = message;

        // 특정 사용자의 세션을 가져와서 메시지를 보냅니다.
        WebSocketSession targetSession = sessionService.getSession(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            messagingTemplate.convertAndSendToUser(targetUserId, "/queue/private", "Call from user in studyroom " + studyroomId);
        }
    }

    @MessageMapping("/studyroom/{studyroomId}/join")
    @SendTo("/topic/studyroom/{studyroomId}")
    public String handleUserJoin(@DestinationVariable String studyroomId, String userId) {
        return "User " + userId + " has joined the room.";
    }

    @MessageMapping("/studyroom/{studyroomId}/leave")
    @SendTo("/topic/studyroom/{studyroomId}")
    public String handleUserLeave(@DestinationVariable String studyroomId, String userId) {
        return "User " + userId + " has left the room.";
    }
}

