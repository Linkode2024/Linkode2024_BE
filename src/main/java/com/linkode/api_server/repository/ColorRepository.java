package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Color;
import com.linkode.api_server.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    Optional<Color> findByColorIdAndStuatus(Long colorId, BaseStatus status);
}
