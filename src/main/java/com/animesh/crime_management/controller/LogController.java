package com.animesh.crime_management.controller;

import com.animesh.crime_management.model.LogEntry;
import com.animesh.crime_management.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogRepository logRepository;

    @GetMapping
    public List<LogEntry> getAllLogs() {
        return logRepository.findAll();
    }

    @GetMapping("/filter/user")
    public List<LogEntry> getLogsByUser(@RequestParam String username) {
        return logRepository.findByPerformedBy(username);
    }

    @GetMapping("/filter/action")
    public List<LogEntry> getLogsByAction(@RequestParam String action) {
        return logRepository.findByAction(action.toUpperCase());
    }

    @GetMapping("/filter/date")
    public List<LogEntry> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return logRepository.findByTimestampBetween(start, end);
    }
}
