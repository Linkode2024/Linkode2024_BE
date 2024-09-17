package com.linkode.api_server.dto.studyroom;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class DataListResponse {

    private List<DataListResponse.Data> DataList;

    @Builder
    public DataListResponse(List<DataListResponse.Data> DataList){
        this.DataList=DataList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data{
        private long dataId;
        private String dataName;
        private String dataUrl;
        private String ogTitle;
        private String ogDescription;
        private String ogImage;
        private String ogUrl;
        private String ogType;
    }


}
