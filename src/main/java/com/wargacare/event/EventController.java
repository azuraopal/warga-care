package com.wargacare.event;

import com.wargacare.common.ApiResponse;
import com.wargacare.common.PagedResponse;
import com.wargacare.event.dto.*;
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
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Endpoint untuk kegiatan/kerja bakti RT/RW")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(summary = "Buat kegiatan baru (ADMIN_RT)")
    public ResponseEntity<ApiResponse<EventResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateEventRequest request) {
        EventResponse response = eventService.create(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Kegiatan berhasil dibuat", response));
    }

    @GetMapping
    @Operation(summary = "Lihat semua kegiatan")
    public ResponseEntity<ApiResponse<PagedResponse<EventResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<EventResponse> response = eventService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Kegiatan berhasil diambil", response));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Lihat kegiatan mendatang")
    public ResponseEntity<ApiResponse<PagedResponse<EventResponse>>> getUpcoming(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<EventResponse> response = eventService.getUpcoming(pageable);
        return ResponseEntity.ok(ApiResponse.success("Kegiatan mendatang berhasil diambil", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail kegiatan")
    public ResponseEntity<ApiResponse<EventResponse>> getById(@PathVariable Long id) {
        EventResponse response = eventService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Detail kegiatan berhasil diambil", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update kegiatan (ADMIN_RT)")
    public ResponseEntity<ApiResponse<EventResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request) {
        EventResponse response = eventService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Kegiatan berhasil diperbarui", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus kegiatan (ADMIN_RT)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Kegiatan berhasil dihapus"));
    }
}
