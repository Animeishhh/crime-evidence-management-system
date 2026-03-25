package com.animesh.crime_management.repository;

import com.animesh.crime_management.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;

public interface LogRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByPerformedBy(String performedBy);
    List<LogEntry> findByAction(String action);
    List<LogEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}