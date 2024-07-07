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
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AvatarRepository avatarRepository;
    private final MemberStudyroomService memberStudyroomService;
    private final TokenService tokenService;

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

    /**
     * 회원탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId){
        log.info("[MemberService.deleteMember]");
        Member member = memberRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberException(NOT_FOUND_MEMBER));
        memberStudyroomService.deleteMember(memberId); // member_studyroom 테이블에서 상태 delete 작업 수행
        member.updateMemberStatus(BaseStatus.DELETE); // member 테이블에서 상태 delete 작업 수행
        memberRepository.save(member);
        tokenService.invalidateToken(member.getGithubId());
        /**
         * TODO : 자료실 delete 작업 -> 아직 자료실이 없어서 구현 불가 추후에 작업
         */
    }
}
