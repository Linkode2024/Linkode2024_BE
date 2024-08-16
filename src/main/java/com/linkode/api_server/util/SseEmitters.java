package com.linkode.api_server.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class SseEmitters {

    private final Map<Long, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    public SseEmitter add(Long studyroomId, SseEmitter emitter) {
        emittersMap.computeIfAbsent(studyroomId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        log.info("New emitter added for studyroomId {}: {}", studyroomId, emitter);
        log.info("Emitter list size for studyroomId {}: {}", studyroomId, emittersMap.get(studyroomId).size());

        emitter.onCompletion(() -> {
            log.info("onCompletion callback for emitter: {}", emitter);
            removeEmitter(studyroomId, emitter);
        });

        emitter.onTimeout(() -> {
            log.info("onTimeout callback for emitter: {}", emitter);
            emitter.complete();
            removeEmitter(studyroomId, emitter);
        });

        return emitter;
    }

    public List<SseEmitter> getEmitters(Long studyroomId) {
        return emittersMap.getOrDefault(studyroomId, new CopyOnWriteArrayList<>());
    }

    private void removeEmitter(Long studyroomId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersMap.get(studyroomId);
        if (emitters != null) {
            emitters.remove(emitter);
            log.info("Emitter removed for studyroomId {}: {}", studyroomId, emitter);
            if (emitters.isEmpty()) {
                emittersMap.remove(studyroomId);
                log.info("All emitters removed for studyroomId {}: {}", studyroomId);
            }
        }
    }
}
