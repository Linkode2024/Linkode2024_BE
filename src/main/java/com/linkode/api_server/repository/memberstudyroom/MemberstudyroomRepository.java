package com.linkode.api_server.repository.memberstudyroom;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface MemberstudyroomRepository extends JpaRepository<MemberStudyroom, Long> {

    @Query("SELECT ms.role FROM MemberStudyroom ms WHERE ms.studyroom.studyroomId = :studyroomId AND ms.member.memberId = :memberId")
    Optional<MemberRole> findRoleByMemberIdAndStudyroomId(long studyroomId, long memberId);

    @Modifying
    @Query("UPDATE MemberStudyroom ms SET ms.status = 'DELETE' WHERE ms.studyroom.studyroomId = :studyroomId")
    int deleteMemberStudyroom(long studyroomId);

    Optional<MemberStudyroom> findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(Long memberId, Long studyroomId, BaseStatus status);
    @Query("SELECT ms " +
            "FROM MemberStudyroom ms " +
            "JOIN FETCH ms.studyroom s " +
            "JOIN FETCH ms.member m " +
            "JOIN FETCH s.memberStudyroomList msl " +
            "JOIN FETCH msl.member " +
            "WHERE s.studyroomId = :studyroomId " +
            "AND m.memberId = :memberId " +
            "AND ms.status = :status")
    Optional<MemberStudyroom> getStudyroomDetail(long studyroomId,long memberId, BaseStatus status);


    /**
     * findByMember_MemberIdAndStatus와 동일하나 N+1 문제 해결버전
     * */
    @Query("SELECT ms FROM MemberStudyroom ms JOIN FETCH ms.studyroom WHERE ms.member.memberId = :memberId AND ms.status = :status")
    Optional<List<MemberStudyroom>> findByMemberIdAndStatus(Long memberId, BaseStatus status);

    boolean existsByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(Long memberId, Long studyroomId, BaseStatus status);
    @Query("SELECT ms FROM MemberStudyroom ms JOIN FETCH ms.studyroom JOIN FETCH ms.member WHERE ms.member.memberId = :memberId AND ms.studyroom.studyroomId =:studyroomId AND ms.status = :status")
    Optional<MemberStudyroom> findByMemberIdAndStudyroomIdAndStatus(Long memberId, Long studyroomId, BaseStatus status);

    @Query("SELECT ms FROM MemberStudyroom ms JOIN FETCH ms.studyroom WHERE ms.member.memberId = :memberId AND ms.status = :status AND ms.studyroom.studyroomId = :studyroomId")
    Optional<MemberStudyroom> findByMemberIdAndStudyroomIdStatus(Long memberId, Long studyroomId, BaseStatus status);

}