package com.linkode.api_server.repository;

import com.linkode.api_server.domain.GithubIssue;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GithubIssueRepository extends JpaRepository<GithubIssue, Long> {
    @Modifying
    @Query("UPDATE GithubIssue gi SET gi.status = :status WHERE gi.studyroom.studyroomId = :studyroomId")
    void updateIssueStatus(Long studyroomId, BaseStatus status);

}
