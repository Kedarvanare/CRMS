package com.crms.backend.repository;

import com.crms.backend.model.Criminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriminalRepository extends JpaRepository<Criminal, String> {
    List<Criminal> findAllByOrderByCreatedAtDesc();
}
