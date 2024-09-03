package com.linkode.api_server.domain;

import com.linkode.api_server.domain.base.BaseStatus;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "studyroom_id")
    private Studyroom studyroom;

    @Builder
    public GithubIssue(String title, String body, String url, String state,BaseStatus status,Studyroom studyroom) {
        this.title = title;
        this.body = body;
        this.url = url;
        this.state = state;
        this.status = status;
        this.studyroom=studyroom;
    }
}
