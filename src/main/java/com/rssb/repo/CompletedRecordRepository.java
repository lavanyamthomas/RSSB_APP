package com.rssb.repo;

import com.rssb.model.CompletedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompletedRecordRepository extends JpaRepository<CompletedRecord, Long>, JpaSpecificationExecutor<CompletedRecord> {}
