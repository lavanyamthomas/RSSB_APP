package com.rssb.assistance.repository;

import com.rssb.assistance.entity.CompletedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompletedRecordRepository extends JpaRepository<CompletedRecord, Long>, JpaSpecificationExecutor<CompletedRecord> {
}
