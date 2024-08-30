package com.linkode.api_server.dto.gitHubIssue;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WebhookURLResponse {
    private String webhookURL;

    @Builder
    public WebhookURLResponse(String webhookURL) {
        this.webhookURL = webhookURL;
    }
}
