package com.linkode.api_server.handler;

import com.linkode.api_server.service.MemberStudyroomService;
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
    private final MemberStudyroomService memberStudyroomService;

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
                    accessor.getSessionAttributes().put("memberId", memberId); //여기에 맴버 아이디를 넣도록함! 이건 클라이언트가 접속시 보내도록 설정이 필요할듯!
                    accessor.setUser(() -> String.valueOf(memberId));

                } catch (Exception e) {
                    log.error("JWT 검증 실패: {}", e.getMessage());
                    throw new IllegalArgumentException("JWT 검증 실패");
                }
            } else if(authorizationHeader == null) {
                log.error("Authorization 헤더가 없습니다.");
                throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            } else if(!authorizationHeader.startsWith("Bearer ")){
                log.error("Authorization 헤더가 이상합니다.");
                throw new IllegalArgumentException("Authorization 헤더 형식이 잘못되었습니다.");
            }
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            // STOMP SUBSCRIBE 시 경로에서 studyroomId 추출 및 검증
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/topic/studyroom/")) {
                String[] parts = destination.split("/"); // 배열이 4이상인지 예를들어 /topic/studyroom/1 형식인지!
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
