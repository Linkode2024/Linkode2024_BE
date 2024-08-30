package com.linkode.api_server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static jakarta.persistence.FetchType.LAZY;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "studyroom_id")
    private Studyroom studyroom;

    @Builder
    public GithubIssue(String title, String body, String url, String state,Studyroom studyroom) {
        this.title = title;
        this.body = body;
        this.url = url;
        this.state = state;
        this.studyroom=studyroom;
    }
}
