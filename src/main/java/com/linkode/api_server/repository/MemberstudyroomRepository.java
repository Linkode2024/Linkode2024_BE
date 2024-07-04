package com.linkode.api_server.repository;

import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberstudyroomRepository extends JpaRepository<MemberStudyroom, Long> {

    @Query("SELECT ms.role FROM MemberStudyroom ms WHERE ms.member.id = :memberId AND ms.studyroom.id = :studyroomId")
    Optional<MemberRole> findRoleByMemberIdAndStudyroomId(long studyroomId, long memberId);

}