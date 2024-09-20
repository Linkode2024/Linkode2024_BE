package com.linkode.api_server.util;

import com.linkode.api_server.dto.gitHubIssue.GithubIssueResponse;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class BroadCaster {

    @Value("${SOCKET_SERVER_URL}")
    private String SOCKET_SERVER_URL ;
    private final RestTemplate restTemplate;

    public void broadCastUploadDataResponse(long studyroomId, long memberId, UploadDataResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("studyroomId", studyroomId);
        body.put("memberId", memberId);
        body.put("event", "fileUploaded");
        body.put("data", response);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(SOCKET_SERVER_URL, request, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Broadcast sent successfully");
            } else {
                log.error("Failed to send broadcast. Status code: " + responseEntity.getStatusCodeValue());
            }
        } catch (RestClientException e) {
            log.error("Error sending broadcast: " + e.getMessage());
        }
    }
    /** 업로드 응답을 메세지로 브로드캐스트 */
    public void broadcastGithubIssue(long studyroomId, GithubIssueResponse issue) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("studyroomId", studyroomId);
        body.put("event", "issueUploaded");
        body.put("data", issue);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(SOCKET_SERVER_URL, request, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Broadcast sent successfully");
            } else {
                log.error("Failed to send broadcast. Status code: " + responseEntity.getStatusCodeValue());
            }
        } catch (RestClientException e) {
            log.error("Error sending broadcast: " + e.getMessage());
        }
    }
}

