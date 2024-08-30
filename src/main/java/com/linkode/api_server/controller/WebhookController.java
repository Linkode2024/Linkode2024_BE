package com.linkode.api_server.controller;

import com.linkode.api_server.dto.gitHubIssue.GithubIssueResponse;
import com.linkode.api_server.service.GithubIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final GithubIssueService githubIssueService;
    private final String BASE_WEBHOOK_URL = "https://www.linkode.site";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/webhook/studyRoomId/{studyRoomId}")
    public void handleGithubWebhook(
            @PathVariable Long studyRoomId,  // 스터디룸 ID를 URL 경로에서 추출
            @RequestBody String payload,
            @RequestHeader("X-GitHub-Event") String event
    ) {
        GithubIssueResponse githubIssueDTO= githubIssueService.saveGithubIssue(studyRoomId,payload);
        messagingTemplate.convertAndSend("/topic/issues/" + studyRoomId, githubIssueDTO);

    }

    @GetMapping("/webhookUrl/studyRoomId/{studyRoomId}")
    public String getWebhookUrl(@PathVariable Long studyRoomId) {
        // 스터디룸 ID에 맞는 웹훅 URL 생성
        return BASE_WEBHOOK_URL + studyRoomId;
    }
}