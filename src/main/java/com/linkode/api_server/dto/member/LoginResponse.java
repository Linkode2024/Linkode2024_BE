package com.linkode.api_server.dto.member;

import com.linkode.api_server.domain.Member;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LoginResponse {
    /**
     * 깃허브 소셜로그인
     */
    private boolean memberStatus;
    private Long memberId;
    private String githubId;
    private String accessToken;
    private String refreshToken;
    private Profile profile;
    private List<Studyroom> studyroomList;

    @Getter
    @Builder
    public static class Profile {
        private String nickname;
        private Long avatarId;
        private Long colorId;

        // 정적 팩토리 메서드
        public static Profile from(Member member) {
            return Profile.builder()
                    .nickname(member.getNickname())
                    .avatarId(member.getAvatar().getAvatarId())
                    .colorId(member.getColor().getColorId())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Studyroom {
        private Long studyroomId;
        private String studyroomProfile;
    }

    // 정적 팩토리 메서드
    public static LoginResponse of(boolean memberStatus, @Nullable Member member, String githubId,String accessToken, String refreshToken, List<Studyroom> studyroomList) {
        return LoginResponse.builder()
                .memberStatus(memberStatus)
                .memberId(member != null ? member.getMemberId() : null)
                .githubId(githubId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .profile(member != null ? Profile.from(member) : null)
                .studyroomList(studyroomList)
                .build();
    }
}
