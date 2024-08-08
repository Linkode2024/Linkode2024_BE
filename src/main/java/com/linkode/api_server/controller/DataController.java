package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.DataException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletionException;

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

    /***
     * CompletionException 라는 비동기 객체의 예외를 먼저 잡고
     * 그 예외가 DataException 라면 그거에 맞는 상태를 반환해 클라이언트가 오류를 잡을 수있도록
     * 예외 처리 강화
     * */
    @PostMapping("/data/upload")
    public BaseResponse<UploadDataResponse> uploadData(@RequestHeader("Authorization") String authorization,
                                                       @ModelAttribute UploadDataRequest request) throws IOException {
        try {
            log.info("[StudyroomController.uploadData]");
            Long memberId = jwtProvider.extractIdFromHeader(authorization);
            UploadDataResponse response = dataService.uploadData(request, memberId).join();

            return new BaseResponse<>(SUCCESS,response);
        }catch (MemberStudyroomException e) {
            return new BaseResponse<>(NOT_FOUND_MEMBER_STUDYROOM, null);
        }catch (CompletionException e){
            Throwable cause = e.getCause();
            if (cause instanceof DataException) {
                DataException de = (DataException) cause;
                return new BaseResponse<>(de.getExceptionStatus(), null);
            }
            return new BaseResponse<>(FAILED_UPLOAD_FILE, null);
        }

    }
}
