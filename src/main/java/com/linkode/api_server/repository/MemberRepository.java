package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByGithubIdAndStatus(String githubId, BaseStatus ACTIVE);
    Optional<Member> findByGithubIdAndStatus(String githubId, BaseStatus ACTIVE);
    Optional<Member> findByMemberIdAndStatus(long memberId, BaseStatus ACTIVE);

}
