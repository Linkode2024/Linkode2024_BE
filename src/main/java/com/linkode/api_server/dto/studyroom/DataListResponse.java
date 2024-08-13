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

    public static DataListResponse of (DataListResponse response){
        return DataListResponse.builder()
                .DataList(response.getDataList())
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class Data{
        private long dataId;
        private String dataName;
        private String dataUrl;
    }


}
