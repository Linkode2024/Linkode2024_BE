package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.service.DataService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

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
        }
    }

}
