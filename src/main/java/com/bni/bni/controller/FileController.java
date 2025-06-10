package com.bni.bni.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.net.MalformedURLException; //dari chatgpt

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "/api/files/" + filename;

            return ResponseEntity.ok().body(
                Map.of(
                    "status", 200,
                    "message", "File uploaded successfully",
                    "filename", filename,
                    "fileUrl", fileUrl
                )
            );
        } catch (IOException e) {
            return ResponseEntity.status(500).body(
                Map.of(
                    "status", 500,
                    "message", "Could not upload the file: " + e.getMessage()
                )
            );
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
