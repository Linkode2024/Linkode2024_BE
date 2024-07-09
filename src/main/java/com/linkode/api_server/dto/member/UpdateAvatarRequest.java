package com.linkode.api_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAvatarRequest {
    private String nickname;
    private Long avatarId;
    private Long colorId;
}
