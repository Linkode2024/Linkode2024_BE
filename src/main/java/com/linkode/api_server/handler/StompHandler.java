package com.linkode.api_server.handler;

import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            // STOMP CONNECT 시 Authorization 헤더에서 JWT를 추출하여 검증
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    // JWT 토큰에서 사용자 정보 추출 및 검증
                    Long memberId = jwtProvider.extractIdFromHeader(authorizationHeader);
                    String githubId = jwtProvider.extractGithubIdFromToken(jwtProvider.extractJwtToken(authorizationHeader));

                    log.info("JWT 검증 성공: memberId={}, githubId={}", memberId, githubId);

                    accessor.getSessionAttributes().put("memberId", memberId);
                    accessor.setUser(() -> String.valueOf(memberId));

                } catch (Exception e) {
                    log.error("JWT 검증 실패: {}", e.getMessage());
                    throw new IllegalArgumentException("JWT 검증 실패");
                }
            } else {
                log.error("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
                throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            }
        }

        return message;
    }
}

