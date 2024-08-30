package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.GithubIssueException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.domain.GithubIssue;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueResponse;
import com.linkode.api_server.repository.GithubIssueRepository;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubIssueService {

    private final GithubIssueRepository githubIssueRepository;
    private final StudyroomRepository studyroomRepository;
    private final MemberstudyroomRepository memberstudyroomRepository;

    public GithubIssueResponse saveGithubIssue(String payload){

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
            return GithubIssueResponse.from(githubIssueRepository.save(issue));
        }else {
            throw new GithubIssueException(ISSUE_PARSING_ERROR);
        }

    }

    public GithubIssueListResponse getGithubIssueList(Long studyroomId, Long memberId){
        log.info("[GithubIssueService.getGithubIssueList]");
        if(!memberstudyroomRepository.existsByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId,studyroomId,BaseStatus.ACTIVE)){
            throw new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM);
        }
        List<GithubIssueListResponse.GithubIssues> issues = studyroomRepository.getIssueListByStudyroom(studyroomId, BaseStatus.ACTIVE)
                .orElseThrow(()->new GithubIssueException(NOT_FOUND_ISSUE));
        return GithubIssueListResponse.builder().IssueList(issues).build();
    }


}
