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


/** 소켓 핸들러 */
@Component
@Slf4j
public class SignalingHandler extends TextWebSocketHandler {

    /** 각사용자의 세션을 스터디룸 아이디 기반으로 그룹화 시킴! */
    private final Map<String, Set<WebSocketSession>> studyroomSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();


    /** 웹 소켓 연결하면 호출되는 함수로 각 유저의 고유한 세션을 생성
     * 유저개별 세션을 유저 아이디 기반으로 관리하기위해 매핑
     * 유저의 개별 세션을 스터디룸이라는 하나의 그룹으로 묶기위해 Set<WebSocketSession>> 을 이용해서 복수의 세션을 묶음
     * */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String studyroomId = getStudyroomId(session);
            String userId = getUserId(session);
            log.info("WebSocket connection established for studyroomId: {}, userId: {}", studyroomId, userId);

            studyroomSessions.computeIfAbsent(studyroomId, k -> ConcurrentHashMap.newKeySet()).add(session);
            userSessions.put(userId + "_" + studyroomId, session); // 유저 ID와 스터디룸 ID를 키로 사용

            session.sendMessage(new TextMessage("Connection Success! for studyroomId : " + studyroomId));
            broadcastMessage(studyroomId, userId, "User " + userId + " has joined the room.");
        } catch (Exception e) {
            log.info("Error during WebSocket connection establishment", e);
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String studyroomId = getStudyroomId(session);
        String userId = getUserId(session);
        String payload = message.getPayload();

        log.info("Received message for studyroomId: {}, userId: {}: {}", studyroomId, userId, message.getPayload());

        if (payload.startsWith("@")) {
            int index = payload.indexOf(':');
            if (index != -1) {
                String targetUserId = payload.substring(1, index);
                String directMessage = payload.substring(index + 1);

                log.info("direct call from {}: {}", targetUserId, directMessage);
                sendMessageToUser(targetUserId + "_" + studyroomId, "[" + userId + " (call)] : " + directMessage);
                return;
            }
        }

        /** 사용 중인 앱 정보 공유 */
        if (payload.startsWith("APP_INFO")) {
            log.info("Broadcasting app info from userId: {}: {}", userId, payload);
            broadcastMessage(studyroomId, userId, payload);
            return;
        }

        // 일반 메시지 브로드캐스트
        broadcastMessage(studyroomId, userId, "[" + userId + "] : " + payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String studyroomId = getStudyroomId(session);
        String userId = getUserId(session);
        log.info("WebSocket connection closed for studyroomId: {}, userId: {}", studyroomId, userId);
        Set<WebSocketSession> sessions = studyroomSessions.get(studyroomId);

        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                studyroomSessions.remove(studyroomId);
            }
        }
        userSessions.remove(userId + "_" + studyroomId);

        broadcastMessage(studyroomId, userId, "User " + userId + " has left the room.");
    }

    /**
     * 소켓 세션의 URI에서 스터디룸 ID 추출
     * URI 형식: ws://localhost:8080/ws?studyroomId=1&userId=1
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

    /**
     * 소켓 세션의 URI에서 유저 ID 추출
     * URI 형식: ws://localhost:8080/ws?studyroomId=1&userId=1
     */
    private String getUserId(WebSocketSession session) throws URISyntaxException {
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
              if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                  return keyValue[1];
              }
          }

          throw new IllegalArgumentException("userId not found in query string");
      }
    /**
     * 특정 사용자에게 메시지 전송
     */
    public void sendMessageToUser(String userId, String message) throws Exception {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            log.warn("No active session found for userId: {}", userId);
        }
    }

    /** 반복문 돌면서 자신의 세션을 제외한 각세션에게 메세지를 뿌림! */
    private void broadcastMessage(String studyroomId, String userId, String message) {
        Set<WebSocketSession> sessions = studyroomSessions.get(studyroomId);
        if (sessions != null) {
            for (WebSocketSession s : sessions) {
                if (!s.getId().equals(userId)) {
                    try {
                        s.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        log.error("Failed to send message to session {}", s.getId(), e);
                    }
                }
            }
        }
    }

}

