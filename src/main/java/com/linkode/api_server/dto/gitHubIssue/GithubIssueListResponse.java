package com.linkode.api_server.dto.gitHubIssue;

import com.linkode.api_server.dto.studyroom.DataListResponse;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class GithubIssueListResponse {

    private List<GithubIssues> IssueList;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GithubIssues{
        private Long githubIssueId;
        private String title;
        private String body;
        private String url;
        private String state;
    }
}
