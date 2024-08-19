package com.linkode.api_server.service;

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
        }else if (request.getFile() != null) {
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
        if(!fileValidater.validateFile(dataName,dataType)){
            throw new DataException(INVALID_EXTENSION);
        }
    }

    /** OpenGraph 데이터 추출 */
    private OpenGraphData extractOpenGraphData(String url) {
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
        Element element = doc.select("meta[property=" + property + "]").first();
        return (element != null) ? element.attr("content") : null;
    }

}
