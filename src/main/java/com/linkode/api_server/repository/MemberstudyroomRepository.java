package com.linkode.api_server.repository;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.DetailStudyroomResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    Optional<List<MemberStudyroom>> findByMember_MemberIdAndStatus(Long memberId, BaseStatus status);
    Optional<List<MemberStudyroom>> findByStudyroom_StudyroomIdInAndStatus(Set<Long> studyroomIds, BaseStatus status);
    @Modifying
    @Query("UPDATE Studyroom s SET s.status = :status WHERE s.studyroomId IN :studyroomIds")
    void updateStudyroomStatus(@Param("studyroomIds") Set<Long> studyroomIds, @Param("status") BaseStatus status);

    @Modifying
    @Query("UPDATE MemberStudyroom ms SET ms.status = :status WHERE ms IN :memberStudyrooms")
    void updateMemberStudyroomStatus(@Param("memberStudyrooms") List<MemberStudyroom> memberStudyrooms, @Param("status") BaseStatus status);

    @Query("SELECT ms.role, m.memberId, m.nickname, m.avatar.id, m.color.id " +
            "FROM MemberStudyroom ms " +
            "JOIN ms.studyroom s " +
            "JOIN s.memberStudyroomList msl " +
            "JOIN msl.member m " +
            "WHERE ms.studyroom.studyroomId = :studyroomId " +
            "AND ms.member.memberId = :memberId " +
            "AND ms.status = :status")
    List<Object[]> getStudyroomDetail(long studyroomId,long memberId, BaseStatus status);


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