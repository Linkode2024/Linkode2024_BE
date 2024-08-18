package com.linkode.api_server.util;

import com.linkode.api_server.service.DataService;
import com.linkode.api_server.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final DataService dataService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        long memberId = jwtProvider.extractIdFromHeader(token);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                URI uri = request.getURI();
                String githubId = jwtProvider.extractGithubIdFromToken(token);
                String studyroomId = extractStudyroomIdFromUri(uri);
                if (tokenService.checkTokenExists(githubId)) {
                    dataService.validateStudyroomMember(memberId,Long.valueOf(studyroomId));
                    attributes.put("githubId", githubId);
                    return true;
                }
            } catch (Exception e) {
                // 인증 실패, 연결 거부
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false; // JWT 토큰이 없거나 유효하지 않으면 연결 거부
    }
    private String extractStudyroomIdFromUri(URI uri) {
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "studyroomId".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        throw new IllegalArgumentException("studyroomId not found in query string");
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
    }
}

