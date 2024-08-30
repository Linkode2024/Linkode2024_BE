package com.linkode.api_server.dto.gitHubIssue;

import com.linkode.api_server.domain.GithubIssue;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GithubIssueResponse {
    private Long githubIssueId;
    private String title;
    private String body;
    private String url;
    private String state;

    @Builder
    public GithubIssueResponse(Long githubIssueId, String title, String body, String url, String state) {
        this.githubIssueId=githubIssueId;
        this.title = title;
        this.body = body;
        this.url = url;
        this.state = state;
    }

    public static GithubIssueResponse from(GithubIssue githubIssue){
        return GithubIssueResponse.builder()
                .githubIssueId(githubIssue.getGithubIssueId())
                .title(githubIssue.getTitle())
                .body(githubIssue.getBody())
                .state(githubIssue.getState())
                .url(githubIssue.getUrl())
                .build();
    }

}
