package com.animesh.crime_management.repository;

import com.animesh.crime_management.model.CaseDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<CaseDetails, Long> {
}