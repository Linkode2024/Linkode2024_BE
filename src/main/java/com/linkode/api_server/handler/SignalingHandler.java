package com.linkode.api_server.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SignalingHandler extends TextWebSocketHandler {

    private final Map<String, Set<WebSocketSession>> studyroomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String studyroomId = getStudyroomId(session);
            log.info("WebSocket connection established for studyroomId: {}", studyroomId);
            studyroomSessions.computeIfAbsent(studyroomId, k -> ConcurrentHashMap.newKeySet()).add(session);

            session.sendMessage(new TextMessage("Connection Success! for studyroomId : " + studyroomId));
        } catch (Exception e) {
            log.info("Error during WebSocket connection establishment", e);
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String studyroomId = getStudyroomId(session);
        log.info("Received message for studyroomId: {}: {}", studyroomId, message.getPayload());

        // 수신한 메시지를 동일한 스터디룸의 모든 세션에 브로드캐스트
        Set<WebSocketSession> sessions = studyroomSessions.get(studyroomId);
        if (sessions != null) {
            for (WebSocketSession s : sessions) {
                if (!s.getId().equals(session.getId())) {
                    s.sendMessage(message);
                }
            }
        }

        session.sendMessage(new TextMessage("My Socket Message" + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String studyroomId = getStudyroomId(session);
        log.info("WebSocket connection closed for studyroomId: {}", studyroomId);
        Set<WebSocketSession> sessions = studyroomSessions.get(studyroomId);

        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                studyroomSessions.remove(studyroomId);
            }
        }
    }

    /**
     * 소켓 세션의 URI에서 스터디룸 ID 추출
     * URI 형식: ws://localhost:8080/ws?studyroomId=1
     */
    private String getStudyroomId(WebSocketSession session) throws URISyntaxException {
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("Session URI is null");
        }

        String query = uri.getQuery();
        if (query == null) {
            throw new IllegalArgumentException("Query string is null");
        }

        String[] queryParams = query.split("&");
        for (String param : queryParams) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "studyroomId".equals(keyValue[0])) {
                return keyValue[1];
            }
        }

        throw new IllegalArgumentException("studyroomId not found in query string");
    }
}
