package com.linkode.api_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    @Value("${github.client-id}")
    private String clientId;
    @Value("${github.client-secret}")
    private String clientSecret;

    @GetMapping("/oauth2/redirect")
    public ResponseEntity<String> githubLogin(@RequestParam String code) {
        String accessToken = getAccessToken(code);
        String userInfo = getUserInfo(accessToken);

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    private String getAccessToken(String code) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://github.com/login/oauth/access_token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                String.class
        );
        log.info(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        // 토큰 저장 하는 redis 구현 해야하는 곳
        return jsonObject.getString("access_token"); // access_token 값만 반환
    }

    private String getUserInfo(String accessToken) {
        String userInfoUri = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                entity,
                String.class
        );

        JSONObject jsonObject = new JSONObject(response.getBody());
        return jsonObject.getString("login");
    }

    private String getUserEmailInfo(String accessToken) {
        String userEmailInfoUri = "https://api.github.com/user/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                userEmailInfoUri,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }
}