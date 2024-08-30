package com.linkode.api_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/app/**").authenticated()  // /app/** 경로로 들어오는 메시지들은 인증된 사용자만 접근 가능
                .anyMessage().authenticated();  // 그 외의 모든 메시지는 인증된 사용자만 접근 가능
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;  // Cross-Origin 설정을 허용하도록 설정
    }
}
