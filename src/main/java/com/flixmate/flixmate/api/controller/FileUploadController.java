package com.flixmate.flixmate.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    // Directory to store uploaded files
    private static final String UPLOAD_DIR = "src/main/webapp/static/images/posters/";
    
    // Maximum file size (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    // Allowed file types
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    @PostMapping("/movie-poster")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadMoviePoster(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("File size too large. Maximum size is 5MB");
            }
            
            // Get file extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("Invalid file name");
            }
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            boolean isValidExtension = false;
            for (String allowedExt : ALLOWED_EXTENSIONS) {
                if (extension.equals(allowedExt)) {
                    isValidExtension = true;
                    break;
                }
            }
            
            if (!isValidExtension) {
                return ResponseEntity.badRequest().body("Invalid file type. Allowed types: JPG, JPEG, PNG, GIF, WEBP");
            }
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(uniqueFilename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return the URL path
            String fileUrl = "/static/images/posters/" + uniqueFilename;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("fileUrl", fileUrl);
            response.put("fileName", uniqueFilename);
            response.put("originalName", originalFilename);
            response.put("fileSize", file.getSize());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/movie-poster/{fileName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMoviePoster(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Files.delete(filePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to delete file: " + e.getMessage());
        }
    }
    
    @GetMapping("/movie-poster/{fileName}")
    public ResponseEntity<?> getMoviePoster(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            
            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(fileContent);
                
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to read file: " + e.getMessage());
        }
    }
}
