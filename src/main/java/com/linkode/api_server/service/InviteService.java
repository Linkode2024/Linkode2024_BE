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
import java.util.Set;
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

    /**
     * 이미 생성된 코드가 있다면 이미 생성된 코드를 사용하도록 하는 코드
     */
    public String generateInviteCode(Long roomId) {
        log.info("[InviteService.generateInviteCode]");
        // 파라미터로 들어온 studyroomId 가 이미 레디스에 존재하는지 확인
        String key = "invite:" + roomId;
        boolean check = redisTemplate.hasKey(key);
        if(check){
            String value = redisTemplate.opsForValue().get(key);
            String[] values = value.split(",");
            // 앞의 code 만 반환
            return values[0];
        }else{
            String code = generateRandomCode();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(validityMinutes);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String value = code + "," + expiryDate.format(formatter);
            // redis 에 접두사를 붙여서 저장하여 로그인 세션과 분리하여 사용할 수 있도록 구현
            redisTemplate.opsForValue().set(key, value, validityMinutes, TimeUnit.MINUTES);
            return code;
        }

    }

    /**
     * 초대 코드로 스터디룸 아이디 찾기
     */
    public Long findRoomIdByInviteCode(String code) {
        String keyPattern = "invite:*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys == null || keys.isEmpty()) {
            return null;
        }

        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                String[] values = value.split(",");
                String storedCode = values[0];
                if (storedCode.equals(code)) {
                    // key 형식이 "invite:{roomId}"이므로, roomId를 추출하여 반환
                    return Long.parseLong(key.split(":")[1]);
                }
            }
        }

        return null;
    }

    /**
     * 그냥 갱신하는 방법
     */
//    public String generateInviteCode(Long roomId) {
//        log.info("[InviteService.generateInviteCode]");
//        String code = generateRandomCode();
//        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(validityMinutes);
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//        String value = code + "," + expiryDate.format(formatter);
//
//        // redis 에 접두사를 붙여서 저장하여 로그인 세션과 분리하여 사용할 수 있도록 구현
//        String key = "invite:" + roomId;
//        redisTemplate.opsForValue().set(key, value, validityMinutes, TimeUnit.MINUTES);
//        return code;
//    }

    private String generateRandomCode() {
        log.info("[InviteService.generateRandomCode]");
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            code.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        return code.toString();
    }
}
