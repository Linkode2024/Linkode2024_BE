package com.linkode.api_server.dto.studyroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataListResponse {

    private List<DataListResponse.Data> DataList;

    @Getter
    @AllArgsConstructor
    public static class Data{
        private long dataId;
        private String dataName;
        private String dataUrl;
    }


}
