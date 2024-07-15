package com.linkode.api_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteService {
    @Value("${invite.code.characters}")
    private String characters;

    @Value("${invite.code.length}")
    private int codeLength;

    @Value("${invite.code.validity.minutes}")
    private int validityMinutes;
    private static final SecureRandom RANDOM = new SecureRandom();
    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generateInviteCode(Long roomId) {
        log.info("[InviteService.generateInviteCode]");
        String code = generateRandomCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(validityMinutes);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String value = roomId + "," + expiryDate.format(formatter);
        // redis 에 접두사를 붙여서 저장하여 로그인 세션과 분리하여 사용할 수 있도록 구현
        String key = "invite:" + code;
        redisTemplate.opsForValue().set(key, value, validityMinutes, TimeUnit.MINUTES);
        return code;
    }

    private String generateRandomCode() {
        log.info("[InviteService.generateRandomCode]");
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            code.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        return code.toString();
    }
}
