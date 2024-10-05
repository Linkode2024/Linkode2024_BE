package com.linkode.api_server.controller;

import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.service.DataService;
import com.linkode.api_server.util.BroadCaster;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/studyroom/data")
public class DataController {
    private final DataService dataService;
    private final JwtProvider jwtProvider;
    private final BroadCaster broadCaster;
    /**
     * 자료 업로드
     */
    @PostMapping("/upload")
    public BaseResponse<UploadDataResponse> uploadData(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @ModelAttribute UploadDataRequest request) {

        log.info("[StudyroomController.uploadData]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        UploadDataResponse response = dataService.uploadData(request, memberId,idempotencyKey);
        broadCaster.broadCastUploadDataResponse(request.getStudyroomId(),memberId,response);
        return new BaseResponse<>(response);
    }
    /**
     * 자료실 조회
     */
    @GetMapping("/list")
    public BaseResponse<DataListResponse> getDataList(@RequestHeader("Authorization") String authorization,
                                                      @RequestParam long studyroomId, @RequestParam DataType type,
                                                      @RequestParam(required = false) Long lastDataId,
                                                      @RequestParam int limit) {
        log.info("[StudyroomController.getDataList]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        DataListResponse response = dataService.getDataList(memberId, studyroomId, type, lastDataId, limit);
        return new BaseResponse<>(response);
    }
}
