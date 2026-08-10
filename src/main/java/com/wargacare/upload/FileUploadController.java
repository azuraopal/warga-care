package com.wargacare.upload;

import com.wargacare.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "Upload", description = "Endpoint upload file gambar dari device user ke server backend")
public class FileUploadController {

    private final Path rootUploadDir = Paths.get("uploads");

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".svg",
            ".heic", ".heif", ".tiff", ".tif", ".avif"
    );

    public FileUploadController() {
        try {
            Files.createDirectories(rootUploadDir);
            Files.createDirectories(rootUploadDir.resolve("events"));
            Files.createDirectories(rootUploadDir.resolve("reports"));
            Files.createDirectories(rootUploadDir.resolve("general"));
        } catch (IOException e) {
            System.err.println("Gagal membuat direktori upload: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file gambar", description = "Upload file gambar dari device user (JPG, PNG, HEIC, WEBP, dll), tersimpan rapih di folder backend")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File yang diupload tidak boleh kosong"));
        }

        String targetSubFolder = folder.toLowerCase().trim();
        if (!targetSubFolder.equals("events") && !targetSubFolder.equals("reports")) {
            targetSubFolder = "general";
        }

        try {
            Path targetDir = rootUploadDir.resolve(targetSubFolder);
            Files.createDirectories(targetDir);

            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg"; 
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Format file tidak didukung: " + extension +
                                ". Format yang didukung: JPG, PNG, HEIC, HEIF, WEBP, GIF, BMP, TIFF, AVIF"));
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path destination = targetDir.resolve(newFilename);

            Files.copy(file.getInputStream(), destination);

            String fileUrl = "/uploads/" + targetSubFolder + "/" + newFilename;
            Map<String, String> responseData = Map.of(
                    "url", fileUrl,
                    "filename", newFilename,
                    "folder", targetSubFolder,
                    "originalName", originalFilename != null ? originalFilename : newFilename
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("File berhasil diupload", responseData));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Gagal menyimpan file: " + e.getMessage()));
        }
    }
}
