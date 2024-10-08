package com.linkode.api_server.repository.data;

import com.linkode.api_server.domain.data.Data;
import com.linkode.api_server.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DataRepository extends JpaRepository<Data, Long>{

    Optional<List<Data>> findByStudyroom_StudyroomIdInAndStatus(Set<Long> studyroomIds, BaseStatus status);

    @Modifying
    @Query("UPDATE Data d SET d.status = :status WHERE d IN :dataLists")
    void updateDataStatus(@Param("dataLists") List<Data> dataLists, @Param("status") BaseStatus status);

    @Modifying
    @Query("UPDATE Data d SET d.status = :status WHERE d.studyroom.studyroomId = :studyroomId")
    void updateStudyroomDataStatus(Long studyroomId, BaseStatus status);
}