package com.linkode.api_server.util;

import com.linkode.api_server.dto.gitHubIssue.GithubIssueResponse;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class BroadCaster {

    @Value("${SOCKET_SERVER_URL}")
    private String SOCKET_SERVER_URL ;

    /** 업로드 응답을 메세지로 브로드캐스트 */
    public void broadCastUploadDataResponse(long studyroomId, long memberId, UploadDataResponse response) {
        WebClient webClient = WebClient.builder().baseUrl(SOCKET_SERVER_URL).build();

        Map<String, Object> body = new HashMap<>();
        body.put("studyroomId", studyroomId);
        body.put("memberId", memberId);
        body.put("event", "fileUploaded");
        body.put("data", response);


        webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        null,
                        e -> log.error("Error sending broadcast : {}", e.getMessage()),
                        () -> log.info("Broadcast sent successfully: studyroomId = {}", studyroomId)
                );
    }

    /** 업로드 응답을 메세지로 브로드캐스트 */
    public void broadcastGithubIssue(long studyroomId, GithubIssueResponse issue) {
        WebClient webClient = WebClient.builder().baseUrl(SOCKET_SERVER_URL).build();

        Map<String, Object> body = new HashMap<>();
        body.put("studyroomId", studyroomId);
        body.put("event", "issueUploaded");
        body.put("data", issue);

        webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        null,
                        error -> log.error("브로드캐스트 전송 중 오류 발생: {}", error.getMessage()),
                        () -> log.info("브로드캐스트 성공적으로 전송됨: studyroomId={}", studyroomId)
                );
    }
}

