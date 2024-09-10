package com.linkode.api_server.repository.githubIssue;

import com.linkode.api_server.domain.QGithubIssue;
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
public class GithubIssueDSL implements GithubIssueRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QGithubIssue githubIssue = QGithubIssue.githubIssue;
    @Override
    public List<GithubIssueListResponse.GithubIssues> getIssueListByStudyroom(Long studyroomId, BaseStatus status, Long lastgithubIssueId, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(githubIssue.studyroom.studyroomId.eq(studyroomId))
                .and(githubIssue.status.eq(status));

        if(lastgithubIssueId!=null){
            builder.and(githubIssue.githubIssueId.lt(lastgithubIssueId));
        }

        return queryFactory.select(Projections.fields(GithubIssueListResponse.GithubIssues.class,
                        githubIssue.githubIssueId,
                        githubIssue.title,
                        githubIssue.body,
                        githubIssue.url,
                        githubIssue.state))
                .from(githubIssue)
                .where(builder)
                .orderBy(githubIssue.githubIssueId.desc())
                .limit(limit)
                .fetch();
    }
}
