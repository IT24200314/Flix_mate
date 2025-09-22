package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.model.ProfileDTO;
import com.flixmate.flixmate.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ProfileDTO profile = userService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody ProfileDTO profileDTO) {
        String email = userDetails.getUsername();
        userService.updateProfile(email, profileDTO);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAdminStats() {
        return ResponseEntity.ok("Admin stats: User count = 2"); // Replace with actual logic
    }
}
