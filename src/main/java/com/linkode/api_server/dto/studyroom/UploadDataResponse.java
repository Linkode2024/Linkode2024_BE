package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.data.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UploadDataResponse {

    private Long dataId;
    private String dataName;
    private DataType dataType;
    private String dataUrl;
}
