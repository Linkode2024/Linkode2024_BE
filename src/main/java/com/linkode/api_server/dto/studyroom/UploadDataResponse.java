package com.linkode.api_server.dto.studyroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UploadDataResponse {

    private Long dataId;
    private String dataName;
    private String dataType;
    private String dataUrl;
}
