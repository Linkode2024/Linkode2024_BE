package com.linkode.api_server.util;

import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
@RequiredArgsConstructor
@Component
@Slf4j
public class S3Uploader {

    private final AmazonS3 amazonS3;
    @Value("${spring.s3.bucket-name}")
    private String bucketName;
    @Value("${spring.cloudfront.domain-name}")
    private String cloudFrontDomainName;

    /**
     * @Async를 메서드에 붙여서 해당 작업을 비동기적으로 수행하도록 하였습니다.
     * 별도의 스레드에서 작업이 진행됩니다.
     * CompletableFuture은 비동기 작업이 완료된후 값을 가져올 수 있게합니다.
     * InputStream으로 입출력을 처리합니다.
     * */
    @Async
    public CompletableFuture<String> uploadFileToS3(MultipartFile file, String folder) throws IOException {
        log.info("[S3Uploader.uploadFileToS3]");
        String fileName = folder + UUID.randomUUID().toString() + "_" + file.getOriginalFilename(); /** 템플릿 코드 : 고유한 아이디를 부여하는 코드라고 합니다! */
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));
        }
        String fileUrl = "https://" + cloudFrontDomainName + "/" + fileName;
        return CompletableFuture.completedFuture(fileUrl);
    }

}
