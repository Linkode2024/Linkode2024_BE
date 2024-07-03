package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByGithubIdAndStatus(String githubId, BaseStatus ACTIVE);
    long findByGithubIdAndStatus(String githubId, BaseStatus ACTIVE);
}
