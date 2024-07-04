package com.linkode.api_server.service;

import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class StudyroomService {

    @Autowired
    MemberstudyroomRepository memberstudyroomRepository;

    @Autowired
    StudyroomRepository studyroomRepository;

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
            if(studyroomRepository.deleteStudyroom(studyroomId)==1){
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
}
