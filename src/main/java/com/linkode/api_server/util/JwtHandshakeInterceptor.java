package com.linkode.api_server.util;


import com.linkode.api_server.service.DataService;
import com.linkode.api_server.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
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
                                   Map<String, Object> attributes) {

        log.info("[JwtHandshakeInterceptor.beforeHandshake]");

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            Cookie[] cookies = servletRequest.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    log.info("Found cookie : {} = {}", cookie.getName(), cookie.getValue());

                    if ("token".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        log.info("Extracted token: {}", token);

                        if (token != null) {
                            try {
                                long memberId = jwtProvider.extractIdFromHeader("Bearer " + token);/**  */
                                String githubId = jwtProvider.extractGithubIdFromToken(token);
                                String studyroomId = extractStudyroomIdFromUri(request.getURI());
                                log.info("Extracted memberId: {}", memberId);

                                if (tokenService.checkTokenExists(githubId)) {
                                    dataService.validateStudyroomMember(memberId, Long.valueOf(studyroomId));
                                    attributes.put("memberId", String.valueOf(memberId));
                                    log.info("Socket Auth Success!");
                                    return true;
                                } else {
                                    log.error("Token does not exist!");
                                }
                            } catch (Exception e) {
                                log.error("Authentication failed", e);
                                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                                return false;
                            }
                        } else {
                            log.error("Token is null");
                        }
                    } else {
                        log.info("!! No 'token' Cookie found !!");
                    }
                }
            } else {
                log.error("!! No cookies found in the request !!");
            }
        }

        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
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