package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.service.DataService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_DATA;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/studyroom/data")
public class DataController {
    DataService dataService;
    JwtProvider jwtProvider;

    @PostMapping("/upload")
    public BaseResponse<UploadDataResponse> uploadData(
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute UploadDataRequest request) {

        log.info("[StudyroomController.uploadData]");
        try {
            Long memberId = jwtProvider.extractIdFromHeader(authorization);
            UploadDataResponse response = dataService.uploadData(request, memberId);
            return new BaseResponse<>(SUCCESS, response);
        }catch (DataException de){
            return new BaseResponse<>(de.getExceptionStatus(), null);
        }catch (MemberStudyroomException me){
            return new BaseResponse<>(me.getExceptionStatus(), null);
    /**
     * 자료실 조회
     */
    @GetMapping("/list")
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
