package com.linkode.api_server.repository;

import com.linkode.api_server.domain.data.Data;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {

    @Query("Select d from Data d WHERE d.studyroom.studyroomId = :studyroomId AND d.dataType = :type AND d.status = :status")
    Optional<List<Data>> getDataListByType(Long studyroomId, DataType type , BaseStatus status);

    @Query("SELECT new com.linkode.api_server.dto.studyroom.DataListResponse$Data(d.dataId, d.dataName, d.dataUrl) " +
            "FROM Data d WHERE d.studyroom.studyroomId = :studyroomId AND d.dataType = :type AND d.status = :status")
    Optional<List<DataListResponse.Data>> getDataListByType2(Long studyroomId, DataType type , BaseStatus status);

}
