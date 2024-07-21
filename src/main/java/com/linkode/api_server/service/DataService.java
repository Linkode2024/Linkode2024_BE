package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_DATA;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataService {

    private final DataRepository dataRepository;

    public DataListResponse getDataList(long studyroomId, DataType type){
        List<DataListResponse.Data> dataList= dataRepository.getDataListByType(studyroomId,type, BaseStatus.ACTIVE)
                .orElseThrow(()->new DataException(NOT_FOUND_DATA))
                .stream().map(data -> new DataListResponse.Data(
                        data.getDataId(),
                        data.getDataName(),
                        data.getDataUrl()
                )).collect(Collectors.toList());


        return new DataListResponse(dataList);
    }

}
