package com.linkode.api_server.dto.data;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OpenGraphData {
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String ogType;

    @Builder
    public OpenGraphData(String ogTitle, String ogDescription, String ogImage, String ogType) {
        this.ogTitle = ogTitle;
        this.ogDescription = ogDescription;
        this.ogImage = ogImage;
        this.ogType = ogType;
    }
}
