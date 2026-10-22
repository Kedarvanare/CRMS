package com.crms.backend.repository;

import com.crms.backend.model.CaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<CaseRecord, String> {
    List<CaseRecord> findAllByOrderByCreatedAtDesc();
}
