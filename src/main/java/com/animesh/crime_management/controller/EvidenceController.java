package com.animesh.crime_management.controller;

import com.animesh.crime_management.model.Evidence;
import com.animesh.crime_management.model.LogEntry;
import com.animesh.crime_management.repository.EvidenceRepository;
import com.animesh.crime_management.repository.LogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("/evidence")
public class EvidenceController {

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private LogRepository logRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // ✅ CREATE Evidence
    @PostMapping(consumes = {"multipart/form-data"})
    @Transactional
    public Evidence addEvidence(
            @RequestPart("evidence") Evidence evidence,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        if (file != null && !file.isEmpty()) {
            String url = saveFile(file);
            evidence.setFileUrl(url);
        }

        evidence.setCreatedBy(getCurrentUser());
        evidence.setCreatedAt(LocalDateTime.now());

        Evidence saved = evidenceRepository.save(evidence);

        LogEntry log = new LogEntry();
        log.setAction("CREATE");
        log.setPerformedBy(getCurrentUser());
        log.setEntityType("Evidence");
        log.setEntityId(saved.getId());
        
        try {
            log.setNewValue(objectMapper.writeValueAsString(saved));
        } catch (Exception e) {
            e.printStackTrace();
        }

        logRepository.save(log);

        return saved;
    }

    // ✅ GET Evidence by Case
    @GetMapping("/case/{caseId}")
    public List<Evidence> getEvidenceByCase(@PathVariable Long caseId) {
        return evidenceRepository.findByCaseId(caseId);
    }

    // ✅ GET ALL Evidence (Descending sequence)
    @GetMapping
    public List<Evidence> getAllEvidenceDescending() {
        return evidenceRepository.findAllByOrderByIdDesc();
    }

    // ✅ UPDATE Evidence (NEW 🔥)
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @Transactional
    public Evidence updateEvidence(
            @PathVariable Long id, 
            @RequestPart("evidence") Evidence updated,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        Evidence evidence = evidenceRepository.findById(id).orElseThrow();
        String previousValueJson = "";
        try {
            previousValueJson = objectMapper.writeValueAsString(evidence);
        } catch (Exception e) {}

        evidence.setDescription(updated.getDescription());
        if (updated.getType() != null) evidence.setType(updated.getType());
        
        if (file != null && !file.isEmpty()) {
            String url = saveFile(file);
            evidence.setFileUrl(url);
        }

        evidence.setUpdatedBy(getCurrentUser());
        evidence.setUpdatedAt(LocalDateTime.now());

        Evidence saved = evidenceRepository.save(evidence);

        LogEntry log = new LogEntry();
        log.setAction("UPDATE");
        log.setPerformedBy(getCurrentUser());
        log.setEntityType("Evidence");
        log.setEntityId(saved.getId());
        log.setPreviousValue(previousValueJson);
        
        try {
            log.setNewValue(objectMapper.writeValueAsString(saved));
        } catch (Exception e) {}

        logRepository.save(log);

        return saved;
    }

    // ✅ DELETE Evidence
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteEvidence(@PathVariable Long id) {
        Evidence evidence = evidenceRepository.findById(id).orElseThrow();
        String previousValueJson = "";
        try {
            previousValueJson = objectMapper.writeValueAsString(evidence);
        } catch (Exception e) {}
        
        evidenceRepository.deleteById(id);

        LogEntry log = new LogEntry();
        log.setAction("DELETE");
        log.setPerformedBy(getCurrentUser());
        log.setEntityType("Evidence");
        log.setEntityId(id);
        log.setPreviousValue(previousValueJson);

        logRepository.save(log);
    }
    
    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    private String saveFile(MultipartFile file) throws IOException {
        String folder = "uploads/";
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(folder + fileName);
        Files.write(path, file.getBytes());
        return path.toString();
    }
}