package com.linkode.api_server.controller;

import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.service.GithubIssueService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/studyroom/issue")
public class GithubIssueController {

    private final GithubIssueService githubIssueService;
    private final JwtProvider jwtProvider;

    @GetMapping("")
    public BaseResponse<GithubIssueListResponse> getIssueList(@RequestHeader("Authorization") String authorization,
                                                              @RequestParam long studyroomId,
                                                              @RequestParam(required = false) Long lastGithubIssueId,
                                                              @RequestParam int limit){
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        GithubIssueListResponse response = githubIssueService.getGithubIssueList(studyroomId,memberId,lastGithubIssueId,limit);
        return new BaseResponse<>(response);
    }

}
