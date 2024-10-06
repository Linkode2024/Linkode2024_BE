package com.linkode.api_server.util;

import com.linkode.api_server.common.exception.DataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.CONFLICT_UPLOAD;

@RequiredArgsConstructor
@Component
@Slf4j
public class IdempotencyKeyProvider {

    private final RedisTemplate<String, Object> redisTemplate;

    /** 멱등키는 클라이언트가 업로드시 세션에 1-2초간 가지고있음
     * 업로드시 헤더에 포함*/
    public void idempotencyKeyValidater(String idempotencyKey){
        if (redisTemplate.hasKey(idempotencyKey)) {
            throw new DataException(CONFLICT_UPLOAD);
        }
    }

    /** 멱등키 레디스에 저장*/
    public void idempotencyKeySetter(String idempotencyKey, Long id){
        redisTemplate.opsForValue().set(idempotencyKey, id, 15, TimeUnit.SECONDS);

    }
}
