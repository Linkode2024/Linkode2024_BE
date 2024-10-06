package com.linkode.api_server.util;

import com.linkode.api_server.dto.data.OpenGraphData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class OpenGraphDataExtracter {
    /** OpenGraph 데이터 추출 */
    public OpenGraphData extractOpenGraphData(String url) {
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
