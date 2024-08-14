package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.data.DataType;
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

    @Builder
    public UploadDataResponse(Long dataId, String dataName, DataType dataType, String dataUrl) {
        this.dataId = dataId;
        this.dataName = dataName;
        this.dataType = dataType;
        this.dataUrl = dataUrl;
    }
}
