package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.exception.StudyroomException;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.*;
import com.linkode.api_server.repository.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import com.linkode.api_server.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyroomService {

    private final StudyroomRepository studyroomRepository;
    private final MemberstudyroomRepository memberstudyroomRepository;
    private final MemberRepository memberRepository;
    private final InviteService inviteService;
    private final S3Uploader s3Uploader;

    private static final String S3_FOLDER = "studyroom_profile/"; // 스터디룸 파일과 구분하기위한 폴더 지정
    @Value("${spring.s3.default-profile}")
    private String DEFAULT_PROFILE;

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

    /** 스터디룸 생성
     * 사진 업로드 안할 시 기본 이미지 */
    @Transactional
    public CreateStudyroomResponse createStudyroom(CreateStudyroomRequest request, long memberId) throws IOException {
        log.info("Start createStudyroom method of StudyroomService Class");
        String fileUrl = getProfileUrl(request.getStudyroomProfile());
        Studyroom studyroom = new Studyroom(request.getStudyroomName(), fileUrl, BaseStatus.ACTIVE);
        studyroomRepository.save(studyroom);
        log.info("Success Create Studyroom");
        joinStudyroomAsCaptain(studyroom.getStudyroomId(),memberId);
        return new CreateStudyroomResponse( studyroom.getStudyroomId(), studyroom.getStudyroomName(),
                studyroom.getStudyroomProfile());

    }

    /** 이미지 URL 얻는 메소드 분리 */
    private String getProfileUrl(MultipartFile file) throws IOException {
        if(file==null || file.isEmpty()){
            return DEFAULT_PROFILE;
        }else{
            CompletableFuture<String> fileUrlFuture = s3Uploader.uploadFileToS3(file, S3_FOLDER);
            try {
                return fileUrlFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to upload file to S3", e);
                throw new IOException("Failed to upload file to S3", e);
            }
        }
    }

    /** 방장으로 가입 */
    @Transactional
    public void joinStudyroomAsCaptain(long studyroomId, long memberId){
        JoinStudyroomRequest joinStudyroomRequest = new JoinStudyroomRequest(studyroomId, memberId, MemberRole.CAPTAIN);
        joinStudyroom(joinStudyroomRequest);
        log.info("Success Join Studyroom as Captain");
    }

    /** 초대 코드로 가입 */
    @Transactional
    public JoinStudyroomByCodeResponse joinStudyroomByCode(JoinStudyroomByCodeRequest request, long memberId){
        log.info("[StudyroomService.joinStudyroomByCode]");
                long studyroomId = inviteService.findRoomIdByInviteCode(request.getInviteCode());
                Studyroom studyroom = studyroomRepository
                        .findById(studyroomId).orElseThrow(()->new StudyroomException(INVALID_INVITE_CODE));
                if (memberstudyroomRepository.findByMemberIdAndStudyroomIdStatus(memberId,studyroomId,BaseStatus.ACTIVE).isPresent()){
                    throw new MemberException(JOINED_STUDYROOM);
                }

               JoinStudyroomRequest joinStudyroomRequest = new JoinStudyroomRequest(studyroomId,
                        memberId, MemberRole.CREW);
               joinStudyroom(joinStudyroomRequest);
        return new JoinStudyroomByCodeResponse(studyroomId,studyroom.getStudyroomName(),studyroom.getStudyroomProfile());
    }


    /** 초대 코드가 필요없는 가입 */
    @Transactional
    public void joinStudyroom(JoinStudyroomRequest request){
        log.info("[StudyroomService.joinStudyroom]");
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
        log.info("Success save memberStudyroom");
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
