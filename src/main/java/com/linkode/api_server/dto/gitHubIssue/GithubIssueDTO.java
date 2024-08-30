package com.linkode.api_server.dto.gitHubIssue;

import com.linkode.api_server.domain.GithubIssue;
import lombok.Builder;

public class GithubIssueDTO {
    private Long id;
    private String title;
    private String body;
    private String url;
    private String state;

    @Builder
    public GithubIssueDTO(Long id, String title, String body, String url, String state) {
        this.id=id;
        this.title = title;
        this.body = body;
        this.url = url;
        this.state = state;
    }

    public static GithubIssueDTO from(GithubIssue githubIssue){
        return GithubIssueDTO.builder()
                .id(githubIssue.getId())
                .title(githubIssue.getTitle())
                .body(githubIssue.getBody())
                .state(githubIssue.getState())
                .url(githubIssue.getUrl())
                .build();
    }

}
