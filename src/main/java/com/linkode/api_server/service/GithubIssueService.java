package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.exception.StudyroomException;
import com.linkode.api_server.domain.GithubIssue;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueResponse;
import com.linkode.api_server.repository.githubIssue.GithubIssueDSL;
import com.linkode.api_server.repository.githubIssue.GithubIssueRepository;
import com.linkode.api_server.repository.memberstudyroom.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GithubIssueService {

    private final GithubIssueRepository githubIssueRepository;
    private final GithubIssueDSL githubIssueDSL;
    private final StudyroomRepository studyroomRepository;
    private final MemberstudyroomRepository memberstudyroomRepository;

    @Transactional
    public GithubIssueResponse saveGithubIssue(Long studyroomId, String payload) {


        JSONObject jsonObject = new JSONObject(payload);

        // 1. PR 제목 (message)
        String prTitle = jsonObject.getJSONObject("head_commit").getString("message");

        // 2. 작성자 정보
        JSONObject authorObject = jsonObject.getJSONObject("head_commit").getJSONObject("author");
        String authorName = authorObject.getString("name");
        String authorUsername = authorObject.getString("username");

        // 3. PR URL
        String prUrl = jsonObject.getJSONObject("head_commit").getString("url");

        // 4. 사용자에게 전달할 메시지 구성
        String message = "작성자: " + authorName + " (" + authorUsername + ")";

        Studyroom studyroom = studyroomRepository.findById(studyroomId)
                .orElseThrow(()->new StudyroomException(NOT_FOUND_STUDYROOM));

        // 5. GithubIssue 객체 생성
        GithubIssue issue = GithubIssue.builder()
                .title(prTitle)
                .body(message)  // 메시지를 body에 넣음
                .url(prUrl)
                .state("opened")
                .studyroom(studyroom)
                .status(BaseStatus.ACTIVE)
                .state("opened") // 상태를 하드코딩하거나 필요한 상태로 대체
                .build();

        // 6. 저장 후 반환
        return GithubIssueResponse.from(githubIssueRepository.save(issue));
    }


    public GithubIssueListResponse getGithubIssueList(Long studyroomId, Long memberId, Long lastGithubIssueId, int limit){
        log.info("[GithubIssueService.getGithubIssueList]");
        if(!memberstudyroomRepository.existsByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId,studyroomId,BaseStatus.ACTIVE)){
            throw new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM);
        }
        List<GithubIssueListResponse.GithubIssues> issues = githubIssueDSL.getIssueListByStudyroom(studyroomId, BaseStatus.ACTIVE,lastGithubIssueId,limit);
        return GithubIssueListResponse.builder().IssueList(issues).build();
    }


}
