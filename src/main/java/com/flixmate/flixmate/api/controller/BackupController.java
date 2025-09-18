package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createBackup() {
        try {
            backupService.createBackup();
            return ResponseEntity.ok("Backup created successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Backup failed: " + e.getMessage());
        }
    }
}
