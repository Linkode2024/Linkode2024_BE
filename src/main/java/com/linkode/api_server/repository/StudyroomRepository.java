package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.dto.DeleteStudyroomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StudyroomRepository extends JpaRepository<Studyroom, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Studyroom sr SET sr.status = 'DELETE' WHERE sr.studyroomId = :studyroomId")
    int deleteStudyroom(long studyroomId);

    @Query("SELECT sr From Studyroom sr WHERE sr.studyroomId = :studyroomId AND sr.status = 'ACTIVE'")
    Optional<Studyroom> findById(long studyroomId);
}