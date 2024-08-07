package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.service.DataService;
import com.linkode.api_server.service.MemberStudyroomService;
import com.linkode.api_server.service.StudyroomService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_DATA;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.SUCCESS;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/studyroom")
public class DataController {
    @Autowired
    DataService dataService;
    @Autowired
    JwtProvider jwtProvider;

    /**
     * 자료실 조회
     */
    @GetMapping("/data/list")
    public BaseResponse<DataListResponse> getDataList(@RequestHeader("Authorization") String authorization,
                                                      @RequestParam long studyroomId, @RequestParam DataType type) {
        log.info("[StudyroomController.getDataList]");
        try {
            Long memberId = jwtProvider.extractIdFromHeader(authorization);
            DataListResponse response = dataService.getDataList(memberId,studyroomId,type);
            return new BaseResponse<>(SUCCESS, response);
        } catch (DataException e) {
            return new BaseResponse<>(NOT_FOUND_DATA, null);
        }
    }
}
