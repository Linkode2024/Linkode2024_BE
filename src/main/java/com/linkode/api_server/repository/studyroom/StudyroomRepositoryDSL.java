package com.linkode.api_server.repository.studyroom;

import com.linkode.api_server.domain.QStudyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
    public List<GithubIssueListResponse.GithubIssues> getIssueListByStudyroom(Long studyroomId, BaseStatus status, Long lastStudyroomId, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(studyroom.studyroomId.eq(studyroomId))
                .and(studyroom.status.eq(status));

        if( lastStudyroomId!=null){
            builder.and(studyroom.studyroomId.lt( lastStudyroomId));
        }

        return queryFactory.select(Projections.fields(GithubIssueListResponse.GithubIssues.class,studyroom.githubIssues))
                .join(studyroom.githubIssues)
                .where(builder)
                .limit(limit)
                .fetch();
    }
}
