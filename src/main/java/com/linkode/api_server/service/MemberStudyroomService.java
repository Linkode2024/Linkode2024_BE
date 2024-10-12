package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.DetailStudyroomResponse;
import com.linkode.api_server.dto.studyroom.MemberStudyroomListResponse;
import com.linkode.api_server.repository.memberstudyroom.MemberstudyroomRepository;
import com.linkode.api_server.repository.memberstudyroom.MemberstudyroomRepositoryDSL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberStudyroomService {

    private final MemberstudyroomRepository memberstudyroomRepository;
    private final MemberstudyroomRepositoryDSL memberstudyroomRepositoryDSL;

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId){
        log.info("[MemberStudyroomService.deleteMember]");
        memberstudyroomRepositoryDSL.deleteMember(memberId, BaseStatus.ACTIVE);
    }

    /**
     * 스터디룸 입장 (기존 회원)
     * */
    public DetailStudyroomResponse getStudyroomDetail(long studyroomId, long memberId) {
        log.info("[MemberStudyroomService.getStudyroomDetail]");
        MemberStudyroom memberStudyroom = memberstudyroomRepository.getStudyroomDetail(studyroomId,memberId,BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));

        MemberRole role = memberStudyroom.getRole();
        String studyroomName = memberStudyroom.getStudyroom().getStudyroomName();
        String studyroomProfile = memberStudyroom.getStudyroom().getStudyroomProfile();

        List<DetailStudyroomResponse.Member> members = memberStudyroom.getStudyroom().getMemberStudyroomList().stream()
                .map(member -> DetailStudyroomResponse.Member.builder()
                        .memberId(member.getMember().getMemberId())
                        .nickname(member.getMember().getNickname())
                        .avatarId(member.getMember().getAvatar().getAvatarId())
                        .colorId(member.getMember().getColor().getColorId())
                        .build())
                .collect(Collectors.toList());

        return DetailStudyroomResponse.builder()
                .role(role)
                .studyroomId(studyroomId)
                .studyroomName(studyroomName)
                .studyroomProfile(studyroomProfile)
                .members(members)
                .build();
    }

    /**
     * 스터디룸 탈퇴
     *
     * memberStudyroom에 대한 유효성 검증을 한후 적절하지 못한 맴버 스터디룸이면 예외를 던집니다.
     * 방장이면 탈퇴할 수 없도록 조건문을 통해 방장인지 파악한 뒤 예외를 강제로 던집니다!
     *
     * */
    @Transactional
    public void leaveStudyroom(long studyroomId, long memberId){
        log.info("[MemberStudyroomService.leaveStudyroom]");
            MemberStudyroom memberStudyroom = memberstudyroomRepository
                    .findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId,studyroomId,BaseStatus.ACTIVE)
                    .orElseThrow(()-> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));
            if(memberStudyroom.getRole()==MemberRole.CAPTAIN) throw new MemberStudyroomException(CANNOT_LEAVE_STUDYROOM);
            memberStudyroom.updateMemberStudyroomStatus(BaseStatus.DELETE);
    }

    /**
     * 유저의 스터디룸 리스트 조회
     *
     * 스트림 문법으로 매핑
     * */
    public MemberStudyroomListResponse getMemberStudyroomList(long memberId){
        log.info("[MemberStudyroomService.getMemberStudyroomList]");
        List<MemberStudyroomListResponse.Studyroom> studyroomList =
                memberstudyroomRepository.findByMemberIdAndStatus(memberId,BaseStatus.ACTIVE)
                        .orElseThrow(()->new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM))
                        .stream()
                        .map(ms -> new MemberStudyroomListResponse.Studyroom(
                                ms.getStudyroom().getStudyroomId(),
                                ms.getStudyroom().getStudyroomProfile()
                        )).collect(Collectors.toList());

        return new MemberStudyroomListResponse(studyroomList);
    }


    /**
     * 스터디룸 리스트 조회
     */
    public MemberStudyroomListResponse getStudyroomList(Long memberId){
        log.info("[StudyroomService.getStudyroomList]");
        MemberStudyroomListResponse latestStudyroomList = getMemberStudyroomList(memberId);
        return latestStudyroomList;
    }
}
