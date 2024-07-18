package com.linkode.api_server.repository;

import com.linkode.api_server.domain.Color;
import com.linkode.api_server.domain.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {
}
