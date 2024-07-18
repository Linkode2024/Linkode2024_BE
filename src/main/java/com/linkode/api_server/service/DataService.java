package com.linkode.api_server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.linkode.api_server.domain.Data;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.studyroom.UploadDataRequest;
import com.linkode.api_server.dto.studyroom.UploadDataResponse;
import com.linkode.api_server.repository.DataRepository;
import com.linkode.api_server.repository.MemberRepository;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final AmazonS3 amazonS3;
    private final MemberRepository memberRepository;
    private final StudyroomRepository studyroomRepository;
    private final DataRepository dataRepository;

    @Value("${spring.s3.bucket-name}")
    private String bucketName;

    public String uploadFileToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));
        }
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public UploadDataResponse uploadData(UploadDataRequest request) throws IOException {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        Studyroom studyroom = studyroomRepository.findById(request.getStudyroomId()).orElseThrow(() -> new IllegalArgumentException("Invalid studyroom ID"));

        String fileUrl = uploadFileToS3(request.getFile());
        String fileName = request.getFile().getOriginalFilename();
        String fileType = request.getDatatype();

        Data data = new Data(fileName, fileType, fileUrl, BaseStatus.ACTIVE, member, studyroom);
        Data savedData = dataRepository.save(data);

        return new UploadDataResponse(savedData.getDataId(), savedData.getDataName(), savedData.getDataType(), savedData.getDataUrl());
    }

}
