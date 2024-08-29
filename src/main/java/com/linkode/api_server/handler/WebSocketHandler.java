package com.linkode.api_server.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // 임시로 세션을 저장할 저장소
    private final ConcurrentMap<String, WebSocketSession> tempSessionStore = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 세션 ID를 키로 하여 세션을 임시 저장소에 저장
        tempSessionStore.put(session.getId(), session);
        log.info("웹소켓 임시저장 시작!!! : sessionId={}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션 종료 시 임시 저장소에서 세션 제거
        tempSessionStore.remove(session.getId());
        log.info("소켓 연결을 종료합니다. 해당 아이디를 임시저장소에서 제거합니다. : sessionId={}", session.getId());
    }

    // STOMP 핸들러에서 임시 세션을 가져와 확정할 수 있도록 제공
    public WebSocketSession getSession(String sessionId) {
        return tempSessionStore.get(sessionId);
    }

    // STOMP 핸들러에서 세션을 확정 등록할 때 사용
    public void confirmSession(String sessionId) {
        WebSocketSession session = tempSessionStore.remove(sessionId);
        if (session != null) {
            log.info("최종적으로 확정하여 세션에 등록합니다. : sessionId={}", sessionId);
            // 여기서 최종적으로 세션을 확정하여 실제 세션 관리 서비스에 등록하는 로직 추가 가능
        } else {
            log.warn("Session not found for sessionId={}", sessionId);
        }
    }

    // 기타 WebSocket 메시지 처리 메서드 추가 가능
}
