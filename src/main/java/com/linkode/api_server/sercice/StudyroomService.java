package com.linkode.api_server.sercice;

import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.DeleteStudyroomRequest;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudyroomService {

    @Autowired
    MemberstudyroomRepository memberstudyroomRepository;

    @Autowired
    StudyroomRepository studyroomRepository;

    public BaseExceptionResponseStatus deleteStudyroom(DeleteStudyroomRequest request, long memberId){

        MemberStudyroom memberStudyroom = memberstudyroomRepository.findByMemberIdAndStudyroomId(memberId,request.getStudyroomId())
                .orElseThrow(()->new IllegalArgumentException("Error because of Invalid Member Id or Invalid StudyRoom Id"));

        if(studyroomRepository.deleteStudyroom(request)==1&&memberStudyroom.getRole().equals(MemberRole.CAPTAIN)){
            return BaseExceptionResponseStatus.SUCCESS;
        }else {
            return BaseExceptionResponseStatus.FAILURE;
        }
    }
}
