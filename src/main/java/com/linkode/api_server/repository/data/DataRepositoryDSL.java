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

import java.util.List;
@Repository
@RequiredArgsConstructor
public class DataRepositoryDSL implements DataRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QData data = QData.data;

    @Override
    public List<DataListResponse.Data> getDataListByType(Long studyroomId, DataType type, BaseStatus status) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(data.studyroom.studyroomId.eq(studyroomId))
                .and(data.dataType.eq(type))
                .and(data.status.eq(status));

        if (type.equals(DataType.LINK)) {
            return queryFactory.select(Projections.fields(DataListResponse.Data.class,
                            data.dataId,
                            data.dataName,
                            data.dataUrl,
                            data.ogTitle,
                            data.ogDescription,
                            data.ogImage,
                            data.ogType,
                            data.ogUrl
                    ))
                    .from(data)
                    .where(builder)
                    .orderBy(data.dataId.desc())
                    .fetch();
        } else {
            return queryFactory.select(Projections.fields(DataListResponse.Data.class,
                            data.dataId,
                            data.dataName,
                            data.dataUrl
                    ))
                    .from(data)
                    .where(builder)
                    .orderBy(data.dataId.desc())
                    .fetch();
        }
    }

}

