package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.data.DataType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UploadDataResponse {

    private Long dataId;
    private String dataName;
    private DataType dataType;
    private String dataUrl;
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String ogUrl;
    private String ogType;

    @Builder
    public UploadDataResponse(Long dataId, String dataName, DataType dataType, String dataUrl, String ogTitle, String ogDescription, String ogImage, String ogUrl, String ogType) {
        this.dataId = dataId;
        this.dataName = dataName;
        this.dataType = dataType;
        this.dataUrl = dataUrl;
        this.ogTitle = ogTitle;
        this.ogDescription = ogDescription;
        this.ogImage = ogImage;
        this.ogUrl = ogUrl;
        this.ogType = ogType;
    }
}
