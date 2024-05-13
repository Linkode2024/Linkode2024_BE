package com.linkode.api_server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthInfo {
    @JsonProperty("access_token")
    private String accessToken;
}
