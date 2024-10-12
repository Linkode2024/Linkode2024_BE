package com.linkode.api_server.repository.memberstudyroom;

import com.linkode.api_server.domain.QGithubIssue;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.QData;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.QMemberStudyroom;
import com.linkode.api_server.domain.QStudyroom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberstudyroomRepositoryDSL implements MemberstudyroomCustom {

    private final JPAQueryFactory queryFactory;
    QMemberStudyroom memberStudyroom = QMemberStudyroom.memberStudyroom;
    QStudyroom studyroom = QStudyroom.studyroom;
    QData data = QData.data;
    QGithubIssue githubIssue = QGithubIssue.githubIssue;

    @Override
    @Transactional
    public void deleteMember(Long memberId, BaseStatus currentStatus) {

        // 1. CAPTAIN이 속한 스터디룸 ID 조회
        var captainStudyroomIds = queryFactory
                .select(memberStudyroom.studyroom.studyroomId)
                .from(memberStudyroom)
                .where(memberStudyroom.member.memberId.eq(memberId)
                        .and(memberStudyroom.status.eq(currentStatus))
                        .and(memberStudyroom.role.eq(MemberRole.CAPTAIN)))
                .fetch();

        // 2. MemberStudyroom 업데이트 (탈퇴하는 멤버의 모든 스터디룸 + CAPTAIN인 경우 해당 스터디룸의 모든 멤버)
        long updatedMemberStudyrooms = queryFactory
                .update(memberStudyroom)
                .set(memberStudyroom.status, BaseStatus.DELETE)
                .where(memberStudyroom.member.memberId.eq(memberId)
                        .and(memberStudyroom.status.eq(currentStatus))
                        .or(memberStudyroom.studyroom.studyroomId.in(captainStudyroomIds)
                                .and(memberStudyroom.status.eq(currentStatus))))
                .execute();

        // 3. CAPTAIN 케이스를 위한 추가 처리
        if (!captainStudyroomIds.isEmpty()) {
            // Data 업데이트
            queryFactory
                    .update(data)
                    .set(data.status, BaseStatus.DELETE)
                    .where(data.studyroom.studyroomId.in(captainStudyroomIds))
                    .execute();

            // GithubIssue 업데이트
            queryFactory
                    .update(githubIssue)
                    .set(githubIssue.status, BaseStatus.DELETE)
                    .where(githubIssue.studyroom.studyroomId.in(captainStudyroomIds))
                    .execute();

            // Studyroom 업데이트
            queryFactory
                    .update(studyroom)
                    .set(studyroom.status, BaseStatus.DELETE)
                    .where(studyroom.studyroomId.in(captainStudyroomIds))
                    .execute();
        }
    }
}