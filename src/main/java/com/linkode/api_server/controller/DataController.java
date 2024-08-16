package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.service.DataService;
import com.linkode.api_server.util.JwtProvider;
import com.linkode.api_server.util.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_DATA;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/studyroom/data")
public class DataController {
    private final DataService dataService;
    private final JwtProvider jwtProvider;
    private final SseEmitters sseEmitters;

    @PostMapping("/upload")
    public BaseResponse<UploadDataResponse> uploadData(
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute UploadDataRequest request) {

        log.info("[StudyroomController.uploadData]");
        try {
            Long memberId = jwtProvider.extractIdFromHeader(authorization);
            UploadDataResponse response = dataService.uploadData(request, memberId);
            for (SseEmitter emitter : sseEmitters.getEmitters(request.getStudyroomId())) {
                try {
                    emitter.send(response);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }
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
    @GetMapping("/list/sse")
    public SseEmitter subscribeDataList(@RequestHeader("Authorization") String authorization, @RequestParam long studyroomId) {
        log.info("[DataController.subscribeDataList]");
        long memberId = jwtProvider.extractIdFromHeader(authorization);
        dataService.validateMember(memberId,studyroomId);
        SseEmitter emitter = new SseEmitter(60000L);/** 커넥션 시간 두시간으로 설정  */
        sseEmitters.add(studyroomId,emitter);
        try {
            /** 처음에 SSE 응답을 할 때 아무런 이벤트도 보내지 않으면 재연결 요청을 보낼때나, 아니면 연결 요청 자체에서 오류가 발생하기때문에 최초 연결 메세지 전달  */
            emitter.send(SseEmitter.event().name("INIT").data("Connection established"));
        }catch (MemberStudyroomException mse){
            emitter.completeWithError(mse);
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

}
