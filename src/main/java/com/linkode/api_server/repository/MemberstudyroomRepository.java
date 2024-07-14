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

    @Query("SELECT ms FROM MemberStudyroom ms " +
            "JOIN FETCH ms.member JOIN FETCH ms.studyroom s " +
            "JOIN FETCH s.memberStudyroomList msl " +
            "JOIN FETCH msl.member " +
            "WHERE ms.studyroom.studyroomId = :studyroomId " +
            "AND ms.member.memberId = :memberId " +
            "AND ms.status = :status")
    Optional<MemberStudyroom> getStudyroomDetail(long studyroomId,long memberId, BaseStatus status);

}