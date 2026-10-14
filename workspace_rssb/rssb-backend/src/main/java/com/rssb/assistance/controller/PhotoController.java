package com.rssb.assistance.controller;

import com.rssb.assistance.dto.PhotoUploadResponse;
import com.rssb.assistance.entity.Photo;
import com.rssb.assistance.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private final PhotoRepository photoRepository;
    private final Path uploadDir;

    public PhotoController(PhotoRepository photoRepository,
                           @Value("${app.upload.dir:uploads}") String uploadDirStr) throws IOException {
        this.photoRepository = photoRepository;
        this.uploadDir = Paths.get(uploadDirStr).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    /**
     * Upload a photo file.
     * Returns the saved photo's ID and accessible URL.
     * POST /api/photos/upload  (multipart/form-data, field name: "file")
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoUploadResponse upload(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, WEBP or GIF images are allowed. Received: " + contentType);
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        // Use a UUID-based filename to prevent path traversal and collisions
        String storedFilename = UUID.randomUUID().toString() + extension;
        Path targetPath = uploadDir.resolve(storedFilename);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = "uploads/" + storedFilename;

        Photo photo = new Photo();
        photo.setFilePath(relativePath);
        photo = photoRepository.save(photo);

        String url = "/api/photos/" + photo.getId() + "/file";
        return new PhotoUploadResponse(photo.getId(), relativePath, url);
    }

    /**
     * Serve an uploaded photo by its DB id.
     * GET /api/photos/{id}/file
     */
    @GetMapping("/{id}/file")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> serveFile(
            @PathVariable Long id) throws IOException {

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found: " + id));

        Path filePath = resolveFilePath(photo.getFilePath());

        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.FileSystemResource(filePath);

        if (!resource.exists()) {
            throw new IllegalArgumentException("File not found for photo id: " + id);
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";

        return org.springframework.http.ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private Path resolveFilePath(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            throw new IllegalArgumentException("Photo path is missing.");
        }

        Path inputPath = Paths.get(storedPath).normalize();
        List<Path> candidates = new ArrayList<>();

        if (inputPath.isAbsolute()) {
            candidates.add(inputPath);
        } else {
            Path fileNameOnly = inputPath.getFileName();
            if (fileNameOnly != null) {
                candidates.add(uploadDir.resolve(fileNameOnly).normalize());
                candidates.add(Paths.get("uploads").toAbsolutePath().normalize().resolve(fileNameOnly).normalize());
                candidates.add(Paths.get("..", "uploads").toAbsolutePath().normalize().resolve(fileNameOnly).normalize());
            }

            candidates.add(uploadDir.resolve(inputPath).normalize());
            if (uploadDir.getParent() != null) {
                candidates.add(uploadDir.getParent().resolve(inputPath).normalize());
            }
        }

        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        throw new IllegalArgumentException("File not found for stored photo path: " + storedPath);
    }
}
