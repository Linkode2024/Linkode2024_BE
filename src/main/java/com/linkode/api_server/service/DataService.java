package com.linkode.api_server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.Data;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.data.OpenGraphData;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.handler.SignalingHandler;
import com.linkode.api_server.repository.DataRepository;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.util.FileValidater;
import com.linkode.api_server.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataService {

    private final MemberstudyroomRepository memberstudyroomRepository;
    private final DataRepository dataRepository;
    private final S3Uploader s3Uploader;
    private final FileValidater fileValidater;
    private final SignalingHandler signalingHandler;
    private static final String S3_FOLDER = "data/"; // 스터디룸 파일과 구분하기위한 폴더 지정

    @Transactional
    public Data saveData(String dataName, DataType dataType, String dataUrl, Member member, Studyroom studyroom) {
        log.info("[DataService.saveData]");
        OpenGraphData openGraphData = (dataType == DataType.LINK) ? extractOpenGraphData(dataUrl) : new OpenGraphData(null, null, null, null);
        Data data = Data.builder()
                .dataName(dataName)
                .dataType(dataType)
                .status(BaseStatus.ACTIVE)
                .studyroom(studyroom)
                .dataUrl(dataUrl)
                .member(member)
                .ogTitle(openGraphData.getOgTitle())
                .ogDescription(openGraphData.getOgDescription())
                .ogImage(openGraphData.getOgImage())
                .ogType(openGraphData.getOgType())
                .build();
        return dataRepository.save(data);
    }

    @Transactional
    public UploadDataResponse uploadData(UploadDataRequest request, long memberId) {
        log.info("[DataService.uploadData]");
        MemberStudyroom memberstudyroom = memberstudyroomRepository.findByMemberIdAndStudyroomIdAndStatus(memberId, request.getStudyroomId(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));
        try {
            String[] dataInfo = extractDataNameAndUrl(request);
            String dataName=dataInfo[0];
            DataType dataType = request.getDataType();
            String dataUrl=dataInfo[1];
            Data savedData = saveData(dataName, dataType, dataUrl, memberstudyroom.getMember(), memberstudyroom.getStudyroom());
                return UploadDataResponse.builder()
                        .dataId(savedData.getDataId())
                        .dataUrl(savedData.getDataUrl())
                        .dataType(savedData.getDataType())
                        .dataName(savedData.getDataName())
                        .ogTitle(savedData.getOgTitle())
                        .ogDescription(savedData.getOgDescription())
                        .ogImage(savedData.getOgImage())
                        .ogType(savedData.getOgType())
                        .build();
        } catch (NullPointerException e) {
            throw new DataException(NONE_FILE);
        }
    }

    public DataListResponse getDataList(long memberId , long studyroomId, DataType type){
        log.info("[DataService.getDataList]");
        if(!memberstudyroomRepository.existsByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId,studyroomId,BaseStatus.ACTIVE)){
            throw new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM);
        }
        List<DataListResponse.Data> dataList= dataRepository.getDataListByType(studyroomId,type, BaseStatus.ACTIVE)
                .orElseThrow(()->new DataException(NOT_FOUND_DATA));
        return new DataListResponse(dataList);
    }

    /** 이름과 URL 추출 */
    private String[] extractDataNameAndUrl(UploadDataRequest request){
        log.info("[DataService.extractDataNameAndUrl]");
        DataType dataType = request.getDataType();
        if(dataType.equals(DataType.LINK) && request.getLink() != null){
            String dataName = request.getLink();
            validateData(dataName,dataType);
            String dataUrl = request.getLink();
            return new String[]{dataName,dataUrl};
        }else if (dataType.equals(DataType.FILE)||dataType.equals(DataType.IMG)) {
            validateType(request);
            String dataName = request.getFile().getOriginalFilename();
            validateData(dataName,dataType);
            String dataUrl = s3Uploader.uploadFileToS3(request.getFile(), S3_FOLDER);
            return new String[]{dataName,dataUrl};
        }
        else {
            throw new DataException(NONE_FILE);
        }
    }

    /** 이름과 타입으로 확장자 검사 */
    private void validateData(String dataName, DataType dataType){
        log.info("[DataService.validateData]");
        if (dataType.equals(DataType.LINK)&&!fileValidater.validateFile(dataName,dataType)){
            throw new DataException(INVALID_URL);
        } else if(!fileValidater.validateFile(dataName,dataType)){
            throw new DataException(INVALID_EXTENSION);
        }
    }

    public String extractJsonResponse(UploadDataResponse response){
        log.info("[DataService.extractJsonResponse]");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info("JAVA -> JSON extract");
            return "DATA_UPLOAD: " + objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("Error JSON extract : ", e);
            return "{ error }";
        }
    }

    /** 맴버가 스터디룸 팀원인지 검증 */
    public void validateStudyroomMember(long memberId, long studyroomId){
        log.info("[DataService.validateStudyroomMember]");
        if(!memberstudyroomRepository.existsByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId,studyroomId,BaseStatus.ACTIVE)){
            throw new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM);
        }
    }

    /** 업로드 응답을 소켓메세지로 브로드캐스트 */
    public void broadCastUploadDataResponse(long studyroomId,long memberId,UploadDataResponse response){
        signalingHandler.broadcastMessage(String.valueOf(studyroomId), String.valueOf(memberId), extractJsonResponse(response));
        log.info("BroadcastMessage of UploadData Success!");
    }
}
    /** 이름과 타입으로 확장자 검사 */
    private void validateType(UploadDataRequest request){
        log.info("[DataService.validateType]");
        if(request.getLink()!=null){ throw new DataException(INVALID_TYPE);}
    }

    /** OpenGraph 데이터 추출 */
    private OpenGraphData extractOpenGraphData(String url) {
        log.info("[DataService.extractOpenGraphData]");
        try {
            Document doc = Jsoup.connect(url).get();
            String ogTitle = getMetaContent(doc, "og:title");
            String ogDescription = getMetaContent(doc, "og:description");
            String ogImage = getMetaContent(doc, "og:image");
            String ogType = getMetaContent(doc, "og:type");

            return new OpenGraphData(ogTitle, ogDescription, ogImage, ogType);
        } catch (IOException e) {
            log.error("Error fetching OpenGraph tags", e);
            return new OpenGraphData(null, null, null, null);
        }
    }

    /** meta[property]에 대한 content 값을 추출 */
    private String getMetaContent(Document doc, String property) {
        log.info("[DataService.getMetaContent]");
        Element element = doc.select("meta[property=" + property + "]").first();
        return (element != null) ? element.attr("content") : null;
    }

}
