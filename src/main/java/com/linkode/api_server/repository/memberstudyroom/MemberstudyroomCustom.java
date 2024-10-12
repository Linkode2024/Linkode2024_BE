package com.linkode.api_server.repository.memberstudyroom;

import com.linkode.api_server.domain.base.BaseStatus;

public interface MemberstudyroomCustom {

    void deleteMember(Long memberId, BaseStatus status);
}
