package com.linkode.api_server.handler;

import com.linkode.api_server.service.MemberStudyroomService;
import com.linkode.api_server.service.WebSocketSessionService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final MemberStudyroomService memberStudyroomService;
    private final WebSocketSessionService webSocketSessionService;
    private final WebSocketHandler webSocketHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    // JWT 토큰에서 사용자 정보 추출 및 검증
                    Long memberId = jwtProvider.extractIdFromHeader(authorizationHeader);
                    String githubId = jwtProvider.extractGithubIdFromToken(jwtProvider.extractJwtToken(authorizationHeader));

                    log.info("JWT 검증 성공: memberId={}, githubId={}", memberId, githubId);
                    accessor.getSessionAttributes().put("memberId", memberId);
                    accessor.setUser(() -> String.valueOf(memberId));

                    // WebSocketHandler에서 임시로 저장된 세션 가져오기
                    WebSocketSession session = webSocketHandler.getSession(accessor.getSessionId());
                    if (session != null) {
                        // 세션을 최종 확정하여 WebSocketSessionService에 등록
                        webSocketHandler.confirmSession(accessor.getSessionId());
                        webSocketSessionService.addSession(String.valueOf(memberId), session);
                        log.info("[웹소켓 세션 등록 완료: memberId = {}]", memberId);
                    } else {
                        log.error("Session not found for sessionId={}", accessor.getSessionId());
                        throw new IllegalArgumentException("WebSocket session not found");
                    }
                } catch (Exception e) {
                    log.error("JWT 검증 실패: {}", e.getMessage());
                    WebSocketSession session = webSocketHandler.getSession(accessor.getSessionId());
                    if (session != null) {
                        try {
                            session.close(CloseStatus.NOT_ACCEPTABLE);  // JWT 검증 실패 시 세션 종료
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    throw new IllegalArgumentException("JWT 검증 실패");
                }
            }
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            // STOMP SUBSCRIBE 시 경로에서 studyroomId 추출 및 검증
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/topic/studyroom/")) {
                String[] parts = destination.split("/"); // 예: /topic/studyroom/1 형식인지 확인
                if (parts.length >= 4) {
                    String studyroomId = parts[3];

                    // 세션에서 memberId를 가져옴
                    Long memberId = (Long) accessor.getSessionAttributes().get("memberId");
                    if (memberId != null) {
                        try {
                            // studyroomId와 memberId를 사용하여 검증
                            memberStudyroomService.validateStudyroomMember(memberId, Long.valueOf(studyroomId));
                            log.info("스터디룸 {}에 대한 구독이 허용되었습니다. memberId={}", studyroomId, memberId);
                        } catch (Exception e) {
                            log.error("스터디룸 {}에 대한 구독이 거부되었습니다. memberId={}, 이유: {}", studyroomId, memberId, e.getMessage());
                            throw new IllegalArgumentException("스터디룸에 대한 접근 권한이 없습니다.");
                        }
                    } else {
                        log.error("세션에 memberId가 없습니다.");
                        throw new IllegalArgumentException("세션에 memberId가 없습니다.");
                    }
                } else {
                    log.error("잘못된 구독 경로: {}", destination);
                    throw new IllegalArgumentException("잘못된 구독 경로입니다.");
                }
            }
        }

        return message;
    }
}
