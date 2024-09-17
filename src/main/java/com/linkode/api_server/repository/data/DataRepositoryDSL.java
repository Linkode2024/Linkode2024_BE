package com.linkode.api_server.repository.data;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.domain.data.QData;
import com.linkode.api_server.dto.studyroom.DataListResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.dsl.Expressions;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class DataRepositoryDSL implements DataRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QData data = QData.data;

    @Override
    public List<DataListResponse.Data> getDataListByType(Long studyroomId, DataType type, BaseStatus status, Long lastDataId, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(data.studyroom.studyroomId.eq(studyroomId))
                .and(data.dataType.eq(type))
                .and(data.status.eq(status));
        if (lastDataId != null) {
            builder.and(data.dataId.lt(lastDataId));
        }
        var projections = Projections.fields(DataListResponse.Data.class,
                data.dataId,
                data.dataName,
                data.dataUrl
        );
        if (type.equals(DataType.LINK)) {
            projections = Projections.fields(DataListResponse.Data.class, data);
        }
        return queryFactory.select(projections)
                .from(data)
                .where(builder)
                .orderBy(data.dataId.desc())
                .limit(limit)
                .fetch();
    }

}

