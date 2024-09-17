package com.linkode.api_server.repository.githubIssue;

import com.linkode.api_server.domain.GithubIssue;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GithubIssueRepository extends JpaRepository<GithubIssue, Long> {

}
