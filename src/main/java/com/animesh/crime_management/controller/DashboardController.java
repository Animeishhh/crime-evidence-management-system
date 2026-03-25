package com.animesh.crime_management.controller;

import com.animesh.crime_management.repository.CaseRepository;
import com.animesh.crime_management.repository.EvidenceRepository;
import com.animesh.crime_management.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private LogRepository logRepository;

    @GetMapping("/stats")
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalCases", caseRepository.count());
        stats.put("totalEvidence", evidenceRepository.count());
        stats.put("totalLogs", logRepository.count());
        return stats;
    }
}
