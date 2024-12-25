package com.linkode.api_server;

import com.linkode.api_server.common.exception.StudyroomException;
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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = {
        OAuth2ClientAutoConfiguration.class
})
@Sql(scripts = "/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // 테스트 메서드 실행 전에 스크립트 실행
public class StudyroomServiceTest {

    @Autowired
    private StudyroomService studyroomService;

    @Autowired
    private StudyroomRepository studyroomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberstudyroomRepository memberstudyroomRepository;

    private Member mockMember;
    private Studyroom studyroom; // mockStudyroom -> studyroom으로 변경
    private JoinStudyroomRequest joinStudyroomRequest;

    private ExecutorService executorService;

    @BeforeEach
    void setup() {
        executorService = Executors.newFixedThreadPool(2);
        mockMember = memberRepository.findById(1L).orElseThrow(() ->
                new RuntimeException("Test data not found. Check if test_data.sql is executed properly."));
        studyroom = studyroomRepository.findById(1L).orElseThrow(() ->
                new RuntimeException("Test data not found. Check if test_data.sql is executed properly."));
        joinStudyroomRequest = new JoinStudyroomRequest(1L, 1L, MemberRole.CREW);
    }

    @AfterEach
    void tearDown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Test
    @DisplayName("5명 중 2명이 동시에 가입 시도 시 1명만 성공")
    void 동시_가입_테스트() throws Exception {
        long initialMemberCount = memberstudyroomRepository.countByStudyroomAndStatus(studyroom, BaseStatus.ACTIVE);

        CountDownLatch latch = new CountDownLatch(2);

        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                try {
                    studyroomService.joinStudyroom(joinStudyroomRequest);
                } catch (StudyroomException | DataIntegrityViolationException e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long finalMemberCount = memberstudyroomRepository.countByStudyroomAndStatus(studyroom, BaseStatus.ACTIVE);
        assertEquals(initialMemberCount + 1, finalMemberCount);
    }

    @Test
    @DisplayName("최대 가입 인원 초과 시 예외 발생")
    void 스터디룸_인원_초과_테스트() {
        for (int i = 0; i < 5; i++) { // 기존 6명에서 5명으로 변경 (본인 포함 6명)
            MemberStudyroom memberStudyroom = new MemberStudyroom(null, BaseStatus.ACTIVE, MemberRole.CREW, mockMember, studyroom);
            memberstudyroomRepository.save(memberStudyroom);
        }

        assertThrows(StudyroomException.class, () -> studyroomService.joinStudyroom(joinStudyroomRequest),
                "최대 가입 인원을 초과하면 예외가 발생해야 합니다.");
    }
}