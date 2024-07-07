package com.linkode.api_server.repository;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MemberstudyroomRepository extends JpaRepository<MemberStudyroom, Long> {

    @Query("SELECT ms.role FROM MemberStudyroom ms WHERE ms.studyroom.studyroomId = :studyroomId AND ms.member.memberId = :memberId")
    Optional<MemberRole> findRoleByMemberIdAndStudyroomId(long studyroomId, long memberId);

    @Modifying
    @Query("UPDATE MemberStudyroom ms SET ms.status = 'DELETE' WHERE ms.studyroom.studyroomId = :studyroomId")
    int deleteMemberStudyroom(long studyroomId);

    Optional<MemberStudyroom> findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(Long memberId, Long studyroomId, BaseStatus status);
    Optional<List<MemberStudyroom>> findByMember_MemberIdAndStatus(Long memberId, BaseStatus status);
    Optional<List<MemberStudyroom>> findByStudyroom_StudyroomIdAndStatus(Long studyroomId, BaseStatus status);
}