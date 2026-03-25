package com.animesh.crime_management.controller;

import com.animesh.crime_management.model.CaseDetails;
import com.animesh.crime_management.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cases")
public class CaseController {

    @Autowired
    private CaseRepository caseRepository;

    // POST API
    @PostMapping
    public CaseDetails createCase(@RequestBody CaseDetails caseDetails) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        caseDetails.setCreatedBy(currentUser);
        caseDetails.setCreatedAt(LocalDateTime.now());
        return caseRepository.save(caseDetails);
    }

    // GET API
    @GetMapping
    public List<CaseDetails> getAllCases() {
        return caseRepository.findAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public CaseDetails getCaseById(@PathVariable Long id) {
        return caseRepository.findById(id).orElseThrow();
    }
}