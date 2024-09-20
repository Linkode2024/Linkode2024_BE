package com.linkode.api_server.controller;

import com.linkode.api_server.dto.gitHubIssue.GithubIssueResponse;
import com.linkode.api_server.dto.gitHubIssue.WebhookURLResponse;
import com.linkode.api_server.service.GithubIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final GithubIssueService githubIssueService;
    private final String BASE_WEBHOOK_URL = "https://www.linkode.site/webhook/studyroomId/";


    @PostMapping("/webhook/studyroomId/{studyroomId}")
    public void handleGithubWebhook(
            @PathVariable Long studyroomId,  // 스터디룸 ID를 URL 경로에서 추출
            @RequestBody String payload,
            @RequestHeader("X-GitHub-Event") String event
    ) {
        GithubIssueResponse githubIssueDTO= githubIssueService.saveGithubIssue(studyroomId,payload);
        githubIssueService.broadcastGithubIssue(studyroomId,githubIssueDTO);

    }

    @GetMapping("/webhookUrl")
    public WebhookURLResponse getWebhookUrl(@RequestParam Long studyroomId) {
        // 스터디룸 ID에 맞는 웹훅 URL 생성
        return WebhookURLResponse .builder().webhookURL(BASE_WEBHOOK_URL + studyroomId).build();
    }
}