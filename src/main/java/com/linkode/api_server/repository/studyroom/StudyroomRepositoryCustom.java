package com.linkode.api_server.repository.studyroom;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;

import java.util.List;
import java.util.Optional;

public interface StudyroomRepositoryCustom {
    List<GithubIssueListResponse.GithubIssues> getIssueListByStudyroom(Long studyroomId, BaseStatus status, Long lastDataId, int limit);

}
