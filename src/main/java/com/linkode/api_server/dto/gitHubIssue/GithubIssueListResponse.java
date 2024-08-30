package com.linkode.api_server.dto.gitHubIssue;

import com.linkode.api_server.dto.studyroom.DataListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GithubIssueListResponse {

    private List<GithubIssues> IssueList;

    @Builder
    @Getter
    public static class GithubIssues{
        private Long gitHubIssueId;
        private String title;
        private String body;
        private String url;
        private String state;
    }
}
