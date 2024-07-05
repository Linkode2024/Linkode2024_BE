package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.dto.CreateStudyroomResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyroomRepository extends JpaRepository<Studyroom, Long> {

}
