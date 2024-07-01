package com.linkode.api_server.service;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.member.LoginResponse;
import com.linkode.api_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    @Value("${SOCIAL_CLIENT_ID}")
    private String clientId;
    @Value("${SOCIAL_CLIENT_SECRET}")
    private String clientSecret;

    private final MemberRepository memberRepository;

    /**
     * 소셜로그인
     */
    public LoginResponse githubLogin(String code){
        log.info("[LoginService.githubLogin]");
        String accessToken = getAccessToken(code);
        String githubId = getUserInfo(accessToken);
        boolean memberStatus = checkMember(githubId);
        return new LoginResponse(memberStatus,githubId);
    }
    private String getAccessToken(String code) {
        log.info("[LoginService.githubLogin.getAccessToken]");
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
        return jsonObject.getString("access_token"); // access_token 값만 반환
    }

    private String getUserInfo(String accessToken) {
        log.info("[LoginService.githubLogin.getUserInfo]");
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

    private boolean checkMember(String githubId){
        log.info("[LoginService.githubLogin.checkMember]");
        boolean memberStatus = memberRepository.existsByGithubIdAndStatus(githubId, BaseStatus.ACTIVE);
        return memberStatus;
    }
}
