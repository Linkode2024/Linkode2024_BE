package com.linkode.api_server.repository.studyroom;

import com.linkode.api_server.domain.QStudyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudyroomRepositoryDSL implements StudyroomRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QStudyroom studyroom = QStudyroom.studyroom;
    @Override
    public List<GithubIssueListResponse.GithubIssues> getIssueListByStudyroom(Long studyroomId, BaseStatus status, Long lastDataId, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(studyroom.studyroomId.eq(studyroomId))
                .and(studyroom.status.eq(status));
        return null;
    }
}
