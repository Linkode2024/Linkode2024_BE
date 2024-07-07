package com.linkode.api_server.service;

import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.dto.studyroom.CreateStudyroomRequest;
import com.linkode.api_server.dto.studyroom.CreateStudyroomResponse;
import com.linkode.api_server.dto.studyroom.JoinStudyroomRequest;
import com.linkode.api_server.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StudyroomService {

    @Autowired
    private StudyroomRepository studyroomRepository;
    @Autowired
    private MemberstudyroomRepository memberstudyroomRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public BaseExceptionResponseStatus deleteStudyroom(long studyroomId, long memberId) {


        if(!studyroomRepository.findById(studyroomId).isPresent()){
            log.info("StudyRoom Id is Invalid");
            return BaseExceptionResponseStatus.FAILURE;
        }

        Optional<MemberRole> optionalMemberRole = memberstudyroomRepository.findRoleByMemberIdAndStudyroomId(studyroomId, memberId);
        if (optionalMemberRole.isEmpty()) {
            log.info("Member Role not found for memberId: " + memberId + " and studyroomId: " + studyroomId);
            return BaseExceptionResponseStatus.FAILURE;
        }
        MemberRole memberRole = optionalMemberRole.orElseThrow(() -> new IllegalArgumentException("Error because of Invalid Member Id or Invalid StudyRoom Id"));

        if (memberRole .equals(MemberRole.CAPTAIN)) {
            if(studyroomRepository.deleteStudyroom(studyroomId)==1 && memberstudyroomRepository.deleteMemberStudyroom(studyroomId)>0){
                log.info("Success delete studyRoom in Service layer");
                return BaseExceptionResponseStatus.SUCCESS;
            }else {
                log.info("Failure delete studyRoom");
                return BaseExceptionResponseStatus.FAILURE;
            }
        } else {
            log.info("Crew Member can't delete studyRoom");
            return BaseExceptionResponseStatus.FAILURE;
        }

    }

    @Transactional
    public CreateStudyroomResponse createStudyroom(CreateStudyroomRequest request, long memberId) {
        log.info("Start createStudyroom method of StudyroomService Class");
        Studyroom studyroom = new Studyroom(
                request.getStudyroomName(),
                request.getStudyroomProfile(),
                BaseStatus.ACTIVE);

        studyroomRepository.save(studyroom);
        log.info("Success Create Studyroom");

        JoinStudyroomRequest joinStudyroomRequest = new JoinStudyroomRequest(studyroom.getStudyroomId()
                ,memberId
                ,MemberRole.CAPTAIN);


        joinStudyroom(joinStudyroomRequest);
        log.info("Success Join Studyroom as Captain");

        return new CreateStudyroomResponse(
                studyroom.getStudyroomId(),
                studyroom.getStudyroomName(),
                studyroom.getStudyroomProfile());
    }

    /** 초대 코드가 필요없음 */
    @Transactional
    public void joinStudyroom(JoinStudyroomRequest request){
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(()->new IllegalArgumentException("Error because of Invalid Member Id"));
        Studyroom studyroom = studyroomRepository.findById(request.getStudyroomId())
                .orElseThrow(()->new RuntimeException("Error because of Invalid StudyRoom Id"));

        MemberStudyroom memberStudyroom = new MemberStudyroom(
                null,
                BaseStatus.ACTIVE,
                request.getMemberRole(),
                member,
                studyroom);

        memberstudyroomRepository.save(memberStudyroom);

    }
}
