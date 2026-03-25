package com.animesh.crime_management.repository;

import com.animesh.crime_management.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    List<Evidence> findByCaseId(Long caseId);
    
    List<Evidence> findAllByOrderByIdDesc();
}