package com.linkode.api_server.repository.githubIssue;

import com.linkode.api_server.domain.GithubIssue;
import com.linkode.api_server.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface GithubIssueRepository extends JpaRepository<GithubIssue, Long> {
    @Modifying
    @Query("UPDATE GithubIssue gi SET gi.status = :status WHERE gi.studyroom.studyroomId = :studyroomId")
    void updateIssueStatus(Long studyroomId, BaseStatus status);
}
