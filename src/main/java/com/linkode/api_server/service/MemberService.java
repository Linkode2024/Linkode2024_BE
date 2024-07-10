package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.ColorException;
import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.domain.Avatar;
import com.linkode.api_server.domain.Color;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.member.*;
import com.linkode.api_server.repository.AvatarRepository;
import com.linkode.api_server.repository.ColorRepository;
import com.linkode.api_server.repository.MemberRepository;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AvatarRepository avatarRepository;
    private final MemberStudyroomService memberStudyroomService;
    private final ColorRepository colorRepository;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;


    /**
     * 캐릭터 생성(회원가입)
     */
    @Transactional
    public CreateAvatarResponse createAvatar(CreateAvatarRequest createAvatarRequest) {
        log.info("[MemberService.createAvatar]");
        String githubId = createAvatarRequest.getGithubId();
        if (memberRepository.existsByGithubIdAndStatus(githubId, BaseStatus.ACTIVE)) {
            throw new MemberException(ALREADY_EXIST_MEMBER);
        } else {
            String nickname = createAvatarRequest.getNickname();
            Long avatarId = createAvatarRequest.getAvatarId();
            Avatar avatar = avatarRepository.findById(avatarId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid avatarId: " + avatarId));
            Long colorId = createAvatarRequest.getColorId();
            Color color = colorRepository.findByColorIdAndStatus(colorId, BaseStatus.ACTIVE)
                    .orElseThrow(()-> new ColorException(NOT_FOUND_COLOR));

            Member member = new Member(githubId, nickname, avatar, color, BaseStatus.ACTIVE);
            memberRepository.save(member);

            String jwtAccessToken = jwtProvider.createAccessToken(githubId);
            String jwtRefreshToken = jwtProvider.createRefreshToken(githubId);
            // 레디스 저장
            tokenService.storeToken(jwtRefreshToken, githubId);
            return new CreateAvatarResponse(jwtAccessToken,jwtRefreshToken);
        }
    }

    /**
     * 회원탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId) {
        log.info("[MemberService.deleteMember]");
        Member member = memberRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        memberStudyroomService.deleteMember(memberId); // member_studyroom 테이블에서 상태 delete 작업 수행
        member.updateMemberStatus(BaseStatus.DELETE); // member 테이블에서 상태 delete 작업 수행
        memberRepository.save(member);
        tokenService.invalidateToken(member.getGithubId());
        /**
         * TODO : 자료실 delete 작업 -> 아직 자료실이 없어서 구현 불가 추후에 작업
         */
    }

    @Transactional
    public void updateAvatar(long memberId, UpdateAvatarRequest request) {
        log.info("[MemberService.updateAvatar]");
        Member member = memberRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Invalid memberId: " + memberId));

        String newNickname = request.getNickname() == null ? member.getNickname() : request.getNickname();

        Avatar newAvatar = request.getAvatarId() == null ? member.getAvatar() : avatarRepository.findById(request.getAvatarId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid avatarId: " + request.getAvatarId()));

        Color newColor = request.getColorId() == null ? member.getColor() : colorRepository.findByColorIdAndStatus(request.getColorId(),BaseStatus.ACTIVE)
                .orElseThrow(()-> new ColorException(NOT_FOUND_COLOR));

        member.updateMemberInfo(newNickname, newAvatar, newColor);

        memberRepository.save(member);

    }

    /**
     * 캐릭터 조회
     */
    public GetAvatarResponse getAvatar(Long memberId){
        log.info("[MemberService.getAvatar]");
        Member member = memberRepository.findByMemberIdWithAvatarAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberException(NOT_FOUND_MEMBER));
        String nickname = member.getNickname();
        Long avatarId= member.getAvatar().getAvatarId();
        Long colorId = member.getColor().getColorId();

        return new GetAvatarResponse(nickname, avatarId, colorId);
    }

    /**
     * 전체 캐릭터 조회
     */
    public GetAvatarAllResponse getAvatarAll(){
        log.info("[MemberService.getAvatarAll]");

        List<GetAvatarAllResponse.Avatar> avatars = avatarRepository.findAll().stream()
                .filter(avatar -> avatar != null && avatar.getAvatarId() != null && avatar.getAvatarImg() != null)
                .map(avatar -> new GetAvatarAllResponse.Avatar(avatar.getAvatarId(), avatar.getAvatarImg()))
                .collect(Collectors.toList());

        List<GetAvatarAllResponse.Color> colors = colorRepository.findAll().stream()
                .filter(color -> color != null && color.getColorId() != null && color.getHexCode() != null)
                .map(color -> new GetAvatarAllResponse.Color(color.getColorId(), color.getHexCode()))
                .collect(Collectors.toList());

        return new GetAvatarAllResponse(avatars, colors);
    }

}
