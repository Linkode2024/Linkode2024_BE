package com.linkode.api_server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.handler.SignalingHandler;
import com.linkode.api_server.service.DataService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_DATA;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/studyroom/data")
public class DataController {
    private final DataService dataService;
    private final JwtProvider jwtProvider;
    private final SignalingHandler signalingHandler;

    @PostMapping("/upload")
    public BaseResponse<UploadDataResponse> uploadData(
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute UploadDataRequest request) {

        log.info("[StudyroomController.uploadData]");
        try {
            Long memberId = jwtProvider.extractIdFromHeader(authorization);
            UploadDataResponse response = dataService.uploadData(request, memberId);
            String jsonResponse = "DATA_UPLOAD: " +extractJsonResponse(response);
            signalingHandler.broadcastMessage(String.valueOf(request.getStudyroomId()), String.valueOf(memberId), jsonResponse);
            return new BaseResponse<>(SUCCESS, response);
        } catch (DataException de) {
            return new BaseResponse<>(de.getExceptionStatus(), null);
        } catch (MemberStudyroomException me) {
            return new BaseResponse<>(me.getExceptionStatus(), null);
        }
    }
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

    private String extractJsonResponse(UploadDataResponse response){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 중 오류 발생: ", e);
            return "{ error }";
        }
    }

}
