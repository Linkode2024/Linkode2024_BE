package com.linkode.api_server.repository.data;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;

import java.util.List;

public interface DataRepositoryCustom {
    List<DataListResponse.Data> getDataListByType(Long studyroomId, DataType type, BaseStatus status);
}
