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

    /**
     * @Async를 메서드에 붙여서 해당 작업을 비동기적으로 수행하도록 하였습니다.
     * 별도의 스레드에서 작업이 진행됩니다.
     * CompletableFuture은 비동기 작업이 완료된후 값을 가져올 수 있게합니다.
     * */
    @Async
    @Transactional
    public CompletableFuture<Data> saveData(String fileName, DataType fileType, String fileUrl, Member member, Studyroom studyroom) {
        log.info("[DataService.saveData]");
        Data data = new Data(fileName, fileType, fileUrl, BaseStatus.ACTIVE, member, studyroom);
        Data savedData = dataRepository.save(data);
        return CompletableFuture.completedFuture(savedData);
    }

    /**
     * 비동기 작업들을 호출한 뒤 join을 통해 작업들을 기다리게 하려했으나 이렇게하면 스레드가 블록킹되어
     * 비동기의 의가 사라지기때문에 체인을 만들어 join을 쓰지않도록하였습니다.
     * 체인 : 기다렸다한다를 명시적으로 정하지않고 자동으로 완료되면 실행되게 작업순서를 엮어둬서 스레드는 I/O작업중에도 블로킹되지않습니다.
     * */
    @Transactional
    public CompletableFuture<UploadDataResponse> uploadData(UploadDataRequest request, long memberId) throws IOException {
        log.info("[DataService.uploadData]");
        MemberStudyroom memberstudyroom = memberstudyroomRepository.findByMemberIdAndStudyroomIdAndStatus(memberId, request.getStudyroomId(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));

        return s3Uploader.uploadFileToS3(request.getFile(),S3_FOLDER)
                .thenCompose(fileUrl -> {
                    String fileName = request.getFile().getOriginalFilename();
                    DataType fileType = request.getDatatype();
                    return saveData(fileName, fileType, fileUrl, memberstudyroom.getMember(), memberstudyroom.getStudyroom());
                })
                .thenApply(savedData -> new UploadDataResponse(savedData.getDataId(), savedData.getDataName(), savedData.getDataType(), savedData.getDataUrl()))
                .exceptionally(ex -> {
                    log.error("Error during upload process!!", ex);
                    throw new DataException(FAILED_UPLOAD_FILE);
                });
    }
}
