package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.Avatar;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.member.CreateAvatarRequest;
import com.linkode.api_server.repository.AvatarRepository;
import com.linkode.api_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.ALREADY_EXIST_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AvatarRepository avatarRepository;

    /**
     * 캐릭터 생성(회원가입)
     */
    @Transactional
    public void createAvatar(CreateAvatarRequest createAvatarRequest){
        log.info("[MemberService.createAvatar]");
        String githubId = createAvatarRequest.getGithubId();
        if(memberRepository.existsByGithubIdAndStatus(githubId, BaseStatus.ACTIVE)){
            throw new MemberException(ALREADY_EXIST_MEMBER);
        } else{
            String nickname = createAvatarRequest.getNickname();
            Long avatarId = createAvatarRequest.getAvatarId();
            Avatar avatar = avatarRepository.findById(avatarId)
                    .orElseThrow(()-> new IllegalArgumentException("Invalid avatarId: " + avatarId));
            String color = createAvatarRequest.getColor();

            Member member = new Member(githubId, nickname, avatar, color, BaseStatus.ACTIVE);
            memberRepository.save(member);
        }
    }
}
