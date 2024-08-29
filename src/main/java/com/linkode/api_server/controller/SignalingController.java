package com.linkode.api_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SignalingController {
    private final SimpMessagingTemplate messagingTemplate;

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

    @MessageMapping("/studyroom/{studyroomId}/call/{targetUserId}")
    @SendTo("/topic/studyroom/{studyroomId}/targetUser/{targetUserId}")
    public Map<String, Object> handleCallMessage(@DestinationVariable String studyroomId,
                                                 @DestinationVariable String targetUserId,
                                                 @Payload Map<String, Object> payload) {
        String callerId = (String) payload.get("callerId");
        if (callerId == null) {
            log.error("Caller ID is null");
            return null;
        }
        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("type", "call");
        messageContent.put("callerId", callerId);
        messageContent.put("studyroomId", studyroomId);
        return messageContent;
    }
}