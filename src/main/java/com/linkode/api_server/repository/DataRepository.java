package com.linkode.api_server.repository;

import com.linkode.api_server.domain.data.Data;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {

    @Query("SELECT new com.linkode.api_server.dto.studyroom.DataListResponse$Data(d.dataId, d.dataName, d.dataUrl, d.ogTitle, d.ogDescription, d.ogImage, d.ogUrl, d.ogType) " +
            "FROM Data d WHERE d.studyroom.studyroomId = :studyroomId AND d.dataType = :type AND d.status = :status " +
            "ORDER BY d.dataId DESC")
    Optional<List<DataListResponse.Data>> getDataListByType(Long studyroomId, DataType type , BaseStatus status);

    @Modifying
    @Query("UPDATE Data d SET d.status = :status WHERE d.studyroom.studyroomId = :studyroomId")
    void updateDataStatus(Long studyroomId, BaseStatus status);

}