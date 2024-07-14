package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.LeaveStudyroomExeption;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.DetailStudyroomResponse;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberStudyroomService {

    private final MemberstudyroomRepository memberstudyroomRepository;

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long memerId){

        log.info("[MemberStudyroomService.deleteMember]");

        List<MemberStudyroom> memberStudyroomList = memberstudyroomRepository.findByMember_MemberIdAndStatus(memerId, BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));

        // CAPTAIN인 스터디룸 ID를 미리 수집합니다.
        Set<Long> captainStudyroomIds = memberStudyroomList.stream()
                .filter(memberStudyroom -> memberStudyroom.getRole() == MemberRole.CAPTAIN)
                .map(memberStudyroom -> memberStudyroom.getStudyroom().getStudyroomId())
                .collect(Collectors.toSet());

        // CAPTAIN의 스터디룸에 속한 모든 CREW를 미리 가져옵니다.
        List<MemberStudyroom> crewList = memberstudyroomRepository.findByStudyroom_StudyroomIdInAndStatus(captainStudyroomIds, BaseStatus.ACTIVE)
                .orElse(Collections.emptyList());

        // CAPTAIN의 스터디룸 상태를 DELETE로 업데이트합니다.
        memberstudyroomRepository.updateStudyroomStatus(captainStudyroomIds, BaseStatus.DELETE);

        // CAPTAIN의 CREW 상태를 DELETE로 업데이트합니다.
        memberstudyroomRepository.updateMemberStudyroomStatus(crewList, BaseStatus.DELETE);

        // 일반 CREW의 상태를 DELETE로 업데이트합니다.
        memberStudyroomList.stream()
                .filter(memberStudyroom -> memberStudyroom.getRole() != MemberRole.CAPTAIN)
                .forEach(memberStudyroom -> memberStudyroom.updateMemberStudyroomStatus(BaseStatus.DELETE));

        memberstudyroomRepository.saveAll(memberStudyroomList);
        memberstudyroomRepository.saveAll(crewList);
    }

    /**
     * 스터디룸 입장 (기존 회원)
     * */
    public DetailStudyroomResponse getStudyroomDetail(long studyroomId, long memberId){
        MemberStudyroom memberStudyroom = memberstudyroomRepository.getStudyroomDetail(studyroomId,memberId,BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));

        /** DTO의 맴버에 맴버엔티티 매핑 */
        List<DetailStudyroomResponse.Member> members = memberStudyroom.getStudyroom().getMemberStudyroomList()
                .stream()
                .map(ms -> new DetailStudyroomResponse.Member(
                        ms.getMember().getMemberId(),
                        ms.getMember().getNickname(),
                        ms.getMember().getAvatar().getAvatarId()
                ))
                .collect(Collectors.toList());

        /** DTO 객체 생성*/
        DetailStudyroomResponse response = new DetailStudyroomResponse(memberStudyroom.getMemberStudyroomId()
                ,memberStudyroom.getRole(),members);
        return response;
    }

    /**
     * 스터디룸 탈퇴
     *
     * memberStudyroom에 대한 유효성 검증을 한후 적절하지 못한 맴버 스터디룸이면 예외를 던집니다.
     * 방장이면 탈퇴할 수 없도록 조건문을 통해 방장인지 파악한 뒤 예외를 강제로 던집니다!
     *
     * */
    @Transactional
    public BaseExceptionResponseStatus leaveStudyroom(long studyroomId, long memberId){
        try {
            MemberStudyroom memberStudyroom = memberstudyroomRepository
                    .findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId,studyroomId,BaseStatus.ACTIVE)
                    .orElseThrow(()-> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));
            if(memberStudyroom.getRole()==MemberRole.CAPTAIN) throw new
                    LeaveStudyroomExeption(CANNOT_LEAVE_STUDYROOM);
            memberStudyroom.updateMemberStudyroomStatus(BaseStatus.DELETE);
            memberstudyroomRepository.save(memberStudyroom);
            return BaseExceptionResponseStatus.SUCCESS;
        }
        catch (LeaveStudyroomExeption e) {
            log.error("MemberStudyroomException! -> ", e);
            return CANNOT_LEAVE_STUDYROOM;
        }
        catch (MemberStudyroomException e) {
            log.error("MemberStudyroomException! -> ", e);
            return NOT_FOUND_MEMBER_STUDYROOM;
        }
        catch (Exception e){
            return FAILURE;
        }
    }

}
