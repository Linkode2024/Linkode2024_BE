package com.linkode.api_server.service;

import com.linkode.api_server.dto.member.CreateAvatarRequest;
import com.linkode.api_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 캐릭터 생성(회원가입)
     */
    public void createAvatar(CreateAvatarRequest createAvatarRequest){
        log.info("[MemberService.createAvatar]");

    }
}
