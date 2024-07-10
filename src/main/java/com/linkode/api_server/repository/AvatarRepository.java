package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Avatar;
import com.linkode.api_server.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByAvatarIdAndStatus(Long avatarId, BaseStatus status);
}
