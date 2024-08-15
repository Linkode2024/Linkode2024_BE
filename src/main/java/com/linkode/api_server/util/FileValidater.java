package com.linkode.api_server.util;

import com.linkode.api_server.domain.data.DataType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileValidater {
    /** 집합으로 정의 */
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>();
    private static final Set<String> FILE_EXTENSIONS = new HashSet<>();
    static {
        IMAGE_EXTENSIONS.add("png");
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("svg");
        IMAGE_EXTENSIONS.add("gif");
        IMAGE_EXTENSIONS.add("TIFF");
        IMAGE_EXTENSIONS.add("HEIC");

        FILE_EXTENSIONS.add("pdf");
        FILE_EXTENSIONS.add("ppt");
        FILE_EXTENSIONS.add("pptx");
        FILE_EXTENSIONS.add("doc");
        FILE_EXTENSIONS.add("docx");
        FILE_EXTENSIONS.add("txt");
        FILE_EXTENSIONS.add("c");
        FILE_EXTENSIONS.add("cpp");
        FILE_EXTENSIONS.add("java");
        FILE_EXTENSIONS.add("html");
        FILE_EXTENSIONS.add("css");
        FILE_EXTENSIONS.add("js");
        FILE_EXTENSIONS.add("hwp");
        FILE_EXTENSIONS.add("xls");
        FILE_EXTENSIONS.add("xlsx");
        FILE_EXTENSIONS.add("xltm");
        FILE_EXTENSIONS.add("cvs");
    }

    public boolean validateFile(String filename, DataType dataType){
        String extension = getFileExtension(filename);
        switch (dataType) {
            case IMG:
                return IMAGE_EXTENSIONS.contains(extension.toLowerCase());
            case FILE:
                return FILE_EXTENSIONS.contains(extension.toLowerCase());
            case LINK:
                return validateUrl(filename);
            default:
                return false;
        }
    }

    /** 파일 확장자 추가 */
    public String getFileExtension(String fileName){
        log.info("FileValidater.getFileExtension");
        log.info("fileExtension : "+fileName.substring(fileName.lastIndexOf(".") + 1));
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /** URL은 별도로 검증 */
    public boolean validateUrl(String url) {
        log.info("FileValidater.validateUrl");
        String urlRegex = "^(https|http)://[^\\s/$.?#].[^\\s]*$";
        return url.matches(urlRegex);
    }

}
