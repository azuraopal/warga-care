package com.wargacare.announcement;

import com.wargacare.announcement.dto.*;
import com.wargacare.common.ApiResponse;
import com.wargacare.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "Announcements", description = "Endpoint untuk pengumuman RT/RW")
@SecurityRequirement(name = "bearerAuth")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    @Operation(summary = "Buat pengumuman baru (ADMIN RT)")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAnnouncementRequest request) {
        AnnouncementResponse response = announcementService.create(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pengumuman berhasil dibuat", response));
    }

    @GetMapping
    @Operation(summary = "Lihat semua pengumuman (pinned di atas)")
    public ResponseEntity<ApiResponse<PagedResponse<AnnouncementResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<AnnouncementResponse> response = announcementService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pengumuman berhasil diambil", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail pengumuman")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getById(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Detail pengumuman berhasil diambil", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pengumuman (ADMIN RT)")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnnouncementRequest request) {
        AnnouncementResponse response = announcementService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Pengumuman berhasil diperbarui", response));
    }

    @PatchMapping("/{id}/pin")
    @Operation(summary = "Sematkan / Lepas sematan pengumuman (ADMIN RT)")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> togglePin(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.togglePin(id);
        return ResponseEntity.ok(ApiResponse.success("Status sematan berhasil diubah", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus pengumuman (ADMIN RT)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Pengumuman berhasil dihapus"));
    }
}
