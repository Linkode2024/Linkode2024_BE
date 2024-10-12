package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyroomRepository extends JpaRepository<Studyroom, Long> {
    @Modifying
    @Query("UPDATE Studyroom sr SET sr.status = :status WHERE studyroomId = :studyroomId")
    void updateStudyroomStatus(Long studyroomId, BaseStatus status);

    @Query("SELECT sr From Studyroom sr WHERE sr.studyroomId = :studyroomId AND sr.status = 'ACTIVE'")
    Optional<Studyroom> findById(long studyroomId);

    @Query("SELECT new com.linkode.api_server.dto.gitHubIssue.GithubIssueListResponse$GithubIssues(gi.githubIssueId, gi.title, gi.body, gi.url, gi.state) " +
            "FROM Studyroom sr " +
            "JOIN sr.githubIssues gi " +
            "WHERE sr.studyroomId = :studyroomId AND sr.status = :status " +
            "ORDER BY gi.githubIssueId DESC")
    Optional<List<GithubIssueListResponse.GithubIssues>> getIssueListByStudyroom(Long studyroomId, BaseStatus status);
}