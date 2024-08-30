package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.GithubIssueException;
import com.linkode.api_server.domain.GithubIssue;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueDTO;
import com.linkode.api_server.repository.GithubIssueRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.ISSUE_PARSING_ERROR;

@Service
@RequiredArgsConstructor
public class GithubIssueService {

    private final GithubIssueRepository githubIssueRepository;

    public GithubIssueDTO saveGithubIssue(String payload){

        JSONObject jsonObject = new JSONObject(payload);
        String action = jsonObject.getString("action");

        // 이슈 생성 또는 수정 이벤트 처리
        if ("opened".equals(action) || "edited".equals(action)) {
            JSONObject issueObject = jsonObject.getJSONObject("issue");

            GithubIssue issue = GithubIssue.builder()
                    .title(issueObject.getString("title"))
                    .body(issueObject.getString("body"))
                    .url(issueObject.getString("html_url"))
                    .state(issueObject.getString("state"))
                    .build();
            return GithubIssueDTO.from(githubIssueRepository.save(issue));
        }else {
            throw new GithubIssueException(ISSUE_PARSING_ERROR);
        }

    }


}
