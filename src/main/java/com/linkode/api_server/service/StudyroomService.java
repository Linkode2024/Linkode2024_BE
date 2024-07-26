package com.linkode.api_server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private final StudyroomRepository studyroomRepository;
    @Autowired
    private final MemberstudyroomRepository memberstudyroomRepository;
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final InviteService inviteService;
    private final AmazonS3 amazonS3;

    @Value("${spring.s3.bucket-name}")
    private String bucketName;
    private static final String S3_FOLDER = "studyroom_profile/"; // 스터디룸 파일과 구분하기위한 폴더 지정
    @Value("${spring.s3.default-profile}")
    private String DEFAULT_PROFILE; // 스터디룸 파일과 구분하기위한 폴더 지정


    public String uploadFileToS3(MultipartFile file) throws IOException {
        log.info("[StudyroomService.uploadFileToS3]");
        if(file.isEmpty() || file==null){
            return DEFAULT_PROFILE;
        }
        String fileName = S3_FOLDER + UUID.randomUUID().toString() + "_" + file.getOriginalFilename(); /** 템플릿 코드 : 고유한 아이디를 부여하는 코드라고 합니다! */
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));
        }
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

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
    public CreateStudyroomResponse createStudyroom(CreateStudyroomRequest request, long memberId) throws IOException {
        log.info("Start createStudyroom method of StudyroomService Class");

        String fileUrl = uploadFileToS3(request.getStudyroomProfile());

        Studyroom studyroom = new Studyroom(request.getStudyroomName(), fileUrl, BaseStatus.ACTIVE);
        studyroomRepository.save(studyroom);
        log.info("Success Create Studyroom");

        joinStudyroomAsCaptain(studyroom.getStudyroomId(),memberId);
        return new CreateStudyroomResponse( studyroom.getStudyroomId(), studyroom.getStudyroomName(),
                studyroom.getStudyroomProfile());

    }

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


    /** 초대 코드가 필요없음 */
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
