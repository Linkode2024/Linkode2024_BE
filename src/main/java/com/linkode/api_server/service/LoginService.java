package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.util.JwtProvider;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.member.LoginResponse;
import com.linkode.api_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_MEMBER;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_MEMBER_STUDYROOM;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {
    @Value("${SOCIAL_CLIENT_ID}")
    private String clientId;
    @Value("${SOCIAL_CLIENT_SECRET}")
    private String clientSecret;

    private final MemberRepository memberRepository;
    private final MemberstudyroomRepository memberstudyroomRepository;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    /**
     * 소셜 로그인
     */
    public LoginResponse githubLogin(String code){
        log.info("[LoginService.githubLogin]");
        String githubId = getUserInfo(getAccessToken(code)); //깃허브 서버와 통신해서 유저 정보 받아오기

        Optional<Member> member = memberRepository.findByGithubIdAndStatus(githubId, BaseStatus.ACTIVE);
        log.info("[깃허브 아이디로 member 찾기]");

        String jwtAccessToken = jwtProvider.createAccessToken(member.get());
        log.info("[엑세스토큰 발급~]");
        String jwtRefreshToken = jwtProvider.createRefreshToken(member.get());
        log.info("[리프레시토큰 발급~]");
        // 레디스 저장
        tokenService.storeToken(jwtRefreshToken, githubId);
        LoginResponse.Profile.from(member.get());
        List<LoginResponse.Studyroom> studyroom = memberstudyroomRepository.findByMemberIdAndStatus(member.get().getMemberId(), BaseStatus.ACTIVE)
                .orElseThrow(()->new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM))
                .stream()
                .map(ms -> new LoginResponse.Studyroom(ms.getStudyroom().getStudyroomId(), ms.getStudyroom().getStudyroomProfile()))
                .collect(Collectors.toList());
        log.info("[studyroom stream 으로 찾아서 반환하기]");

        return LoginResponse.of(
                member.get(),
                jwtAccessToken,
                jwtRefreshToken,
                studyroom
        );
    }

    /**
     * 로그아웃
     */
    public BaseExceptionResponseStatus logout(String token){
        log.info("[LoginService.githubLogin.logout]");
        try{
            String githubId = jwtProvider.extractGithubIdFromToken(jwtProvider.extractJwtToken(token));
            Member member = memberRepository.findByGithubIdAndStatus(githubId,BaseStatus.ACTIVE)
                    .orElseThrow(()->new IllegalArgumentException("Error because of Invalid Member Id"));
            tokenService.invalidateToken(githubId);
            log.info("InvalidateToken Success!");
            return BaseExceptionResponseStatus.SUCCESS;
        }catch (IllegalArgumentException e){
            log.info("Logout Failure");
            return BaseExceptionResponseStatus.FAILURE;
        }
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
