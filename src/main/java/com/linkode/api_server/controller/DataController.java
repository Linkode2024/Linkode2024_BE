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

    /***
     * 파일 업로드
     * @RequestParam으로 쓴이유는 파일 업로드는 multipart/form-data 로 일반적 json이 아니기때문입니다.
     * @RequestParam의 해당 값들이 URI에 노출되지않습니다.
     * */
    @PostMapping("/data/upload")
    public BaseResponse<UploadDataResponse> uploadData(@RequestHeader("Authorization") String authorization,
                                                       @RequestParam("studyroomId") long studyroomId,
                                                       @RequestParam("datatype") DataType datatype,
                                                       @RequestParam("file") MultipartFile file) throws IOException {
        try {
            log.info("[DataController.uploadData]");
            Long memberId = jwtProvider.extractIdFromHeader(authorization);
            UploadDataRequest request = new UploadDataRequest(studyroomId, datatype, file);
            UploadDataResponse response = dataService.uploadData(request, memberId).join();

            return new BaseResponse<>(SUCCESS,response);
        }catch (MemberStudyroomException e) {
            return new BaseResponse<>(NOT_FOUND_MEMBER_STUDYROOM, null);
        }catch (DataException e){
            return new BaseResponse<>(FAILED_UPLOAD_FILE,null);
        }

    }
}
