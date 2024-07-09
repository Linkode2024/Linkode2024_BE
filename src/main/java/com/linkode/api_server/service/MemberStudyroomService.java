package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_CREW;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_MEMBER_STUDYROOM;

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
}
