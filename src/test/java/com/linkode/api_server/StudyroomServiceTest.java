package com.linkode.api_server;

import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.dto.studyroom.JoinStudyroomRequest;
import com.linkode.api_server.repository.MemberRepository;
import com.linkode.api_server.repository.StudyroomRepository;
import com.linkode.api_server.repository.memberstudyroom.MemberstudyroomRepository;
import com.linkode.api_server.service.StudyroomService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class StudyroomServiceTest {

    @Autowired
    private StudyroomService studyroomService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyroomRepository studyroomRepository;

    @Autowired
    private MemberstudyroomRepository memberstudyroomRepository;

    @Test
    @DisplayName("joinStudyroom 테스트")
    void joinStudyroom() {
        // given
        Member member = new Member("testGithubId", "testNickname", null, null, BaseStatus.ACTIVE);
        memberRepository.save(member);

        Studyroom studyroom = new Studyroom("Test Room", "room.jpg", BaseStatus.ACTIVE);
        studyroomRepository.save(studyroom);

        JoinStudyroomRequest request = JoinStudyroomRequest.builder()
                .studyroomId(studyroom.getStudyroomId())
                .memberId(member.getMemberId())
                .memberRole(MemberRole.CREW)
                .build();

        // when
        studyroomService.joinStudyroom(request);

        // then
        MemberStudyroom memberStudyroom = memberstudyroomRepository
                .findByMember_MemberIdAndStudyroom_StudyroomIdAndStatus(
                        member.getMemberId(), studyroom.getStudyroomId(), BaseStatus.ACTIVE)
                .orElseThrow();

        assertThat(memberStudyroom.getRole()).isEqualTo(MemberRole.CREW);
        assertThat(memberStudyroom.getMember()).isEqualTo(member);
        assertThat(memberStudyroom.getStudyroom()).isEqualTo(studyroom);
    }
}
