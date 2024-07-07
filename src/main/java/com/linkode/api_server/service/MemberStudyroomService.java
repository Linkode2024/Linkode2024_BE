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

import java.util.List;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_CREW;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_MEMBER_STUDYROOM;

@Slf4j
@Service
@RequiredArgsConstructor
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

        for(MemberStudyroom memberStudyroom : memberStudyroomList) {
            if(memberStudyroom.getRole() == MemberRole.CAPTAIN){
                log.info("[MemberStudyroomService.deleteMember.CAPTAIN_VERSION]");
                Long studyroomId = memberStudyroom.getStudyroom().getStudyroomId();
                // 지우려는 스터디룸에 포함된 crew 들도 delete
                List<MemberStudyroom> crewList = memberstudyroomRepository.findByStudyroom_StudyroomIdAndStatus(studyroomId, BaseStatus.ACTIVE)
                        .orElseThrow(()-> new MemberStudyroomException(NOT_FOUND_CREW));
                for(MemberStudyroom crew : crewList){
                    log.info("[[MemberStudyroomService.deleteMember.CAPTAIN_VERSION.crewDelete]");
                    crew.updateMemberStudyroomStatus(BaseStatus.DELETE);
                    memberstudyroomRepository.save(crew);
                }
                //스터디룸도 delete
                memberStudyroom.getStudyroom().updateStudyroomStatus(BaseStatus.DELETE);
                memberstudyroomRepository.save(memberStudyroom);
            }else{
                log.info("[MemberStudyroomService.deleteMember.CREW_VERSION]");
                memberStudyroom.updateMemberStudyroomStatus(BaseStatus.DELETE);
                memberstudyroomRepository.save(memberStudyroom);
            }
        }
    }
}
