package com.wargacare.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wargacare.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
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


    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    private final Path rootUploadDir = Paths.get("uploads");

    @Value("${app.public-url:}")
    private String publicUrl;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp",
            ".heic", ".heif", ".tiff", ".tif", ".avif"
    );

    public FileUploadController() {
        try {
            Files.createDirectories(rootUploadDir);
            Files.createDirectories(rootUploadDir.resolve("events"));
            Files.createDirectories(rootUploadDir.resolve("reports"));
            Files.createDirectories(rootUploadDir.resolve("general"));
        } catch (IOException e) {
            log.error("Gagal menginisialisasi direktori upload di {}: {}", rootUploadDir.toAbsolutePath(), e.getMessage());
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

            String fileUrl;
            if (publicUrl != null && !publicUrl.isBlank()) {
                String base = publicUrl.trim();
                while (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }
                if (base.endsWith("/api")) {
                    fileUrl = base + "/uploads/" + targetSubFolder + "/" + newFilename;
                } else {
                    fileUrl = base + "/api/uploads/" + targetSubFolder + "/" + newFilename;
                }
            } else {
                fileUrl = "/uploads/" + targetSubFolder + "/" + newFilename;
            }
            Map<String, String> responseData = Map.of(
                    "url", fileUrl,
                    "filename", newFilename,
                    "folder", targetSubFolder,
                    "originalName", originalFilename != null ? originalFilename : newFilename
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("File berhasil diupload", responseData));

        } catch (java.nio.file.AccessDeniedException e) {
            log.error("Izin akses ditolak saat menyimpan file di {}: {}", e.getFile(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Gagal menyimpan file: Izin akses ditolak (Permission Denied) pada folder " + e.getFile() + ". Pastikan volume Docker memiliki izin tulis (write permission)."));
        } catch (IOException e) {
            log.error("Gagal menyimpan file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Gagal menyimpan file: " + e.getMessage()));
        }
    }
}
