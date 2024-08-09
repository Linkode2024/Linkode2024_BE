package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.domain.data.Data;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.repository.DataRepository;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataService {

    private final MemberstudyroomRepository memberstudyroomRepository;
    private final DataRepository dataRepository;
    private final S3Uploader s3Uploader;
    private static final String S3_FOLDER = "data/"; // 스터디룸 파일과 구분하기위한 폴더 지정

    @Async
    @Transactional
    public Data saveData(String fileName, DataType fileType, String fileUrl, Member member, Studyroom studyroom) {
        log.info("[DataService.saveData]");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);
        Data data = new Data(fileName, fileType, fileUrl, BaseStatus.ACTIVE, member, studyroom);
        Data savedData = dataRepository.save(data);
        return savedData;
    }

    @Transactional
    public UploadDataResponse uploadData(UploadDataRequest request, long memberId){
        log.info("[DataService.uploadData]");
        MemberStudyroom memberstudyroom = memberstudyroomRepository.findByMemberIdAndStudyroomIdAndStatus(memberId, request.getStudyroomId(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));
        try {
            String fileName = request.getFile().getOriginalFilename();
            DataType fileType = request.getDatatype();
            String fileUrl = s3Uploader.uploadFileToS3(request.getFile(),S3_FOLDER);
            Data savedData = saveData(fileName, fileType, fileUrl, memberstudyroom.getMember(), memberstudyroom.getStudyroom());
            return new UploadDataResponse(savedData.getDataId(), savedData.getDataName(), savedData.getDataType(), savedData.getDataUrl());
        }catch (NullPointerException e){
            throw new DataException(NONE_FILE);
        }

    }
}
