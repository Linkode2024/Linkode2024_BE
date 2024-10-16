package com.linkode.api_server.service;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.common.exception.MemberStudyroomException;
import com.linkode.api_server.common.exception.StudyroomException;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.*;
import com.linkode.api_server.repository.memberstudyroom.MemberstudyroomRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import com.linkode.api_server.repository.data.DataRepository;
import com.linkode.api_server.repository.githubIssue.GithubIssueRepository;
import com.linkode.api_server.util.FileValidater;
import com.linkode.api_server.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.*;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.FAILED_DELETE_STUDYROOM;

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
    private final FileValidater fileValidater;
    private final DataRepository dataRepository;
    private final GithubIssueRepository githubIssueRepository;

    private static final String S3_FOLDER = "studyroom_profile/"; // 스터디룸 파일과 구분하기위한 폴더 지정
    @Value("${spring.s3.default-profile}")
    private String DEFAULT_PROFILE;

    @Transactional
    public void deleteStudyroom(long studyroomId, long memberId) {
        log.info("[StudyroomService.deleteStudyroom]");
        if (validateMemberRole(studyroomId,memberId,MemberRole.CAPTAIN)) {
            try {
                memberstudyroomRepository.deleteMemberStudyroom(studyroomId,BaseStatus.DELETE);
                dataRepository.updateStudyroomDataStatus(studyroomId,BaseStatus.DELETE);
                githubIssueRepository.updateStudyroomIssueStatus(studyroomId,BaseStatus.DELETE);
                studyroomRepository.updateStudyroomStatus(studyroomId, BaseStatus.DELETE);
            }catch (Exception e){
                log.error("Failure during delete studyroom -> {}",e.getMessage(), e);
                throw new StudyroomException(FAILED_DELETE_STUDYROOM);
            }
        } else {
            log.error("Failure find studyRoom of captain");
            throw new StudyroomException(NOT_FOUND_MEMBERROLE);
        }
    }

    /** 스터디룸 ROLE 검증 메서드 */
    public Boolean validateMemberRole(long studyroomId, long memberId,MemberRole role){
        log.info("[StudyroomService.validateMemberRole]");
        return memberstudyroomRepository.findRoleByMemberIdAndStudyroomIdAndRole(studyroomId, memberId,BaseStatus.ACTIVE)
                .stream()
                .anyMatch(m->m.equals(role));
    }

    /** 스터디룸 생성
     * 사진 업로드 안할 시 기본 이미지 */
    @Transactional
    public CreateStudyroomResponse createStudyroom(CreateStudyroomRequest request, long memberId){
        log.info("Start createStudyroom method of StudyroomService Class");
        String fileUrl = getProfileUrl(request.getStudyroomProfile());
        Studyroom studyroom = new Studyroom(request.getStudyroomName(), fileUrl, BaseStatus.ACTIVE);
        studyroomRepository.save(studyroom);
        log.info("Success Create Studyroom");
        joinStudyroomAsCaptain(studyroom.getStudyroomId(),memberId);
        return CreateStudyroomResponse.from(studyroom);
    }

    /** 이미지 URL 얻는 메소드 분리 */
    private String getProfileUrl(MultipartFile file){
        log.info("StudyroomService.getProfileUrl");

        if(file==null || file.isEmpty()){
            return DEFAULT_PROFILE;
        }else{
            if(!fileValidater.validateFile(file.getOriginalFilename(), DataType.IMG)) {
                throw new DataException(INVALID_EXTENSION);
            }
            return s3Uploader.uploadFileToS3(file, S3_FOLDER);
        }
    }

    /** 방장으로 가입 */
    @Transactional
    public void joinStudyroomAsCaptain(long studyroomId, long memberId){
        JoinStudyroomRequest joinStudyroomRequest = JoinStudyroomRequest.builder()
                        .studyroomId(studyroomId)
                        .memberId(memberId)
                        .memberRole(MemberRole.CAPTAIN)
                        .build();
        joinStudyroom(joinStudyroomRequest);
        log.info("Success Join Studyroom as Captain");
    }

    /** 초대 코드로 가입 */
    @Transactional
    public JoinStudyroomByCodeResponse joinStudyroomByCode(JoinStudyroomByCodeRequest request, long memberId){
        log.info("[StudyroomService.joinStudyroomByCode]");
        Long studyroomId = inviteService.findRoomIdByInviteCode(request.getInviteCode())
                .orElseThrow(()->new StudyroomException(INVALID_INVITE_CODE));
        Studyroom studyroom = studyroomRepository
                .findById(studyroomId).orElseThrow(()->new StudyroomException(INVALID_INVITE_CODE));
        if (memberstudyroomRepository.findByMemberIdAndStudyroomIdStatus(memberId,studyroomId,BaseStatus.ACTIVE).isPresent()){
            throw new MemberException(JOINED_STUDYROOM);
        }
        JoinStudyroomRequest joinStudyroomRequest = JoinStudyroomRequest.builder()
                .studyroomId(studyroomId)
                .memberId(memberId)
                .memberRole(MemberRole.CREW)
                .build();

        joinStudyroom(joinStudyroomRequest);
        return JoinStudyroomByCodeResponse.from(studyroom);
    }


    /** 초대 코드가 필요없는 가입 */
    @Transactional
    public void joinStudyroom(JoinStudyroomRequest request){
        log.info("[StudyroomService.joinStudyroom]");
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(()->new MemberException(NOT_FOUND_MEMBER));
        Studyroom studyroom = studyroomRepository.findById(request.getStudyroomId())
                .orElseThrow(()->new StudyroomException(NOT_FOUND_STUDYROOM));

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
    public CreateStudyroomResponse modifyStudyroom(Long memberId, PatchStudyroomRequest patchStudyroomRequest){
        log.info("[StudyroomService.modifyStudyroom]");
        Long studyroomId = patchStudyroomRequest.getStudyroomId();
        MemberStudyroom memberStudyroom = memberstudyroomRepository.findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(memberId, studyroomId,BaseStatus.ACTIVE)
                .orElseThrow(()->new StudyroomException(NOT_FOUND_MEMBER_STUDYROOM));

        if(memberStudyroom.getRole().equals(MemberRole.CAPTAIN)){
            log.info("[StudyroomService.modifyStudyroom -- MEMBERROLE : CAPTAIN]");
            Studyroom studyroom = studyroomRepository.findById(studyroomId)
                    .orElseThrow(()-> new StudyroomException(NOT_FOUND_STUDYROOM));

            String studyroomName = studyroom.getStudyroomName();
            String studyroomImg = studyroom.getStudyroomProfile();

            if(patchStudyroomRequest.getStudyroomName() != null){
                studyroomName = patchStudyroomRequest.getStudyroomName();
            }

            if(patchStudyroomRequest.getStudyroomImg() != null){
                studyroomImg = getProfileUrl(patchStudyroomRequest.getStudyroomImg());
            }

            studyroom.updateStudyroomInfo(studyroomName,studyroomImg);
            studyroomRepository.save(studyroom);
            return CreateStudyroomResponse.builder()
                    .studyroomId(studyroomId)
                    .studyroomName(studyroomName)
                    .studyroomProfile(studyroomImg)
                    .build();
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
