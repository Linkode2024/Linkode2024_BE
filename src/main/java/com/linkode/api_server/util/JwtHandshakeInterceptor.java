package com.linkode.api_server.util;

import com.linkode.api_server.service.DataService;
import com.linkode.api_server.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final DataService dataService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes){

        log.info("[JwtHandshakeInterceptor.beforeHandshake]");
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("Sec-WebSocket-Protocol");
        long memberId = jwtProvider.extractIdFromHeader("Bearer "+token);

        if (token != null) {
            try {
                URI uri = request.getURI();
                String githubId = jwtProvider.extractGithubIdFromToken(token);
                String studyroomId = extractStudyroomIdFromUri(uri);
                if (tokenService.checkTokenExists(githubId)) {
                    dataService.validateStudyroomMember(memberId,Long.valueOf(studyroomId));
                    attributes.put("memberId", String.valueOf(memberId));
                    log.info("Socket Auth Success!");
                    return true;
                }
            } catch (Exception e) {
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false; /** JWT 토큰이 없거나 유효하지 않으면 연결 거부 */
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