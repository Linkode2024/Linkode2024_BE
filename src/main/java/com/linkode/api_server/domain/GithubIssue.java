package com.linkode.api_server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long githubIssueId;
    private String title;
    private String body;
    private String url;
    private String state;

    @Builder
    public GithubIssue(String title, String body, String url, String state) {
        this.title = title;
        this.body = body;
        this.url = url;
        this.state = state;
    }
}
