package com.linkode.api_server.util;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.linkode.api_server.common.exception.DataException;
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

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.FAILED_UPLOAD_FILE;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NONE_FILE;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3Uploader {

    private final AmazonS3 amazonS3;
    @Value("${spring.s3.bucket-name}")
    private String bucketName;
    @Value("${spring.cloudfront.domain-name}")
    private String cloudFrontDomainName;

    public String uploadFileToS3(MultipartFile file, String folder){
        log.info("[S3Uploader.uploadFileToS3]");
        if(file.isEmpty()|file==null){
            throw new DataException(NONE_FILE);
        }
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = folder + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));
            String fileUrl = "https://" + cloudFrontDomainName + "/" + fileName;
            return fileUrl;
        }catch (NullPointerException e) {
            log.error("File Is NULL !!", e);
            throw new DataException(NONE_FILE);
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new DataException(FAILED_UPLOAD_FILE);
        }

    }

}
