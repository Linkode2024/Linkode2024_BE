package com.linkode.api_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetAvatarAllResponse {
    /**
     * 캐릭터 전체 조회
     */
    private List<Avatar> avatar;
    private List<Color> color;

    @Getter
    @AllArgsConstructor
    public static class Avatar{
        private Long avatarId;
        private String avatarImg;
    }

    @Getter
    @AllArgsConstructor
    public static class Color{
        private Long colorId;
        private String hexCode;
    }
}
