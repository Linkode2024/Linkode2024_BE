package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.exception.StudyroomException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.*;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyroomService {

    @Autowired
    private final StudyroomRepository studyroomRepository;
    @Autowired
    private final MemberstudyroomRepository memberstudyroomRepository;
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final InviteService inviteService;

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

    /** 초대 코드로 가입 */
    @Transactional
    public BaseResponse<JoinStudyroomByCodeResponse> joinStudyroomByCode(JoinStudyroomByCodeRequest request, long memberId){

            try {
                long studyroomId = inviteService.findRoomIdByInviteCode(request.getInviteCode());
                Studyroom studyroom = studyroomRepository
                        .findById(studyroomId).orElseThrow(()->new StudyroomException(INVALID_INVITE_CODE));

               if(memberstudyroomRepository.findByMemberIdAndStudyroomIdStatus(memberId,studyroomId,BaseStatus.ACTIVE).isPresent()){
                   throw new MemberStudyroomException(JOINED_STUDYROOM);
               }
               JoinStudyroomRequest joinStudyroomRequest = new JoinStudyroomRequest(studyroomId,
                        memberId, request.getMemberRole());
                joinStudyroom(joinStudyroomRequest);
                return new BaseResponse<>(new JoinStudyroomByCodeResponse(studyroomId,studyroom.getStudyroomName(),studyroom.getStudyroomProfile()));
            }catch (NullPointerException e){
                return new BaseResponse<>(INVALID_INVITE_CODE,null);
            }catch (StudyroomException e){
                return new BaseResponse<>(INVALID_INVITE_CODE,null);
            }catch (MemberException m){
                return new BaseResponse<>(JOINED_STUDYROOM,null);
            }
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

    /**
     * 스터디룸 수정
     */
    @Transactional
    public void modifyStudyroom(Long memberId, PatchStudyroomRequest patchStudyroomRequest){
        log.info("[StudyroomService.modifyStudyroom]");
        Long studyroomId = patchStudyroomRequest.getStudyroomId();
        MemberStudyroom memberStudyroom = memberstudyroomRepository.findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId, studyroomId,BaseStatus.ACTIVE)
                .orElseThrow(()->new StudyroomException(NOT_FOUND_MEMBERROLE));
        if(memberStudyroom.getRole().equals(MemberRole.CAPTAIN)){
            Studyroom studyroom = studyroomRepository.findById(studyroomId)
                    .orElseThrow(()-> new StudyroomException(NOT_FOUND_STUDYROOM));
            String studyroomName = studyroom.getStudyroomName();
            String studyroomImg = studyroom.getStudyroomProfile();
            if(patchStudyroomRequest.getStudyroomName() != null){
                studyroomName = patchStudyroomRequest.getStudyroomName();
            }
            if(patchStudyroomRequest.getStudyroomImg() != null){
                studyroomImg = patchStudyroomRequest.getStudyroomImg();
            }
            studyroom.updateStudyroomInfo(studyroomName,studyroomImg);
            studyroomRepository.save(studyroom);
        }else{
            throw new StudyroomException(INVALID_ROLE);
        }
    }

    /**
     * 초대코드 생성
     */
    @Transactional
    public PostInviteCodeResponse createStudyroomCode(Long memberId, Long studyroomId){
        log.info("[StudyroomService.createStudyroomCode]");

        // 요청으로 스터디룸에 요청을 보낸 멤버 id 를 가진 멤버가 있는지 확인
        MemberStudyroom memberStudyroom = memberstudyroomRepository.findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId,studyroomId,BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberStudyroomException(NOT_FOUND_MEMBER_STUDYROOM));

        String inviteCode = inviteService.generateInviteCode(studyroomId);
        return new PostInviteCodeResponse(inviteCode);
    }
}
