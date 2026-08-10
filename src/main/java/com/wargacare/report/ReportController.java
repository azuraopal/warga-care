package com.wargacare.report;

import com.wargacare.common.ApiResponse;
import com.wargacare.common.PagedResponse;
import com.wargacare.report.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Endpoint untuk manajemen laporan pengaduan warga")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Daftar kategori laporan", description = "Mendapatkan daftar semua kategori laporan pengaduan")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getCategories() {
        List<Map<String, String>> categories = Arrays.stream(ReportCategory.values())
                .map(cat -> Map.of(
                        "value", cat.name(),
                        "label", getCategoryLabel(cat)
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Daftar kategori berhasil diambil", categories));
    }

    @GetMapping("/statuses")
    @Operation(summary = "Daftar status laporan", description = "Mendapatkan daftar semua status laporan pengaduan")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getStatuses() {
        List<Map<String, String>> statuses = Arrays.stream(ReportStatus.values())
                .map(st -> Map.of(
                        "value", st.name(),
                        "label", getStatusLabel(st)
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Daftar status berhasil diambil", statuses));
    }

    private String getCategoryLabel(ReportCategory cat) {
        return switch (cat) {
            case JALAN_RUSAK -> "Jalan Rusak";
            case SAMPAH -> "Pengelolaan Sampah";
            case LAMPU_MATI -> "Lampu Penerangan Jalan";
            case BANJIR -> "Banjir / Drainase";
            case HEWAN_HILANG -> "Hewan Peliharaan / Liar";
            case BANTUAN_WARGA -> "Bantuan Sosial Warga";
            case KEAMANAN -> "Keamanan / Ketertiban";
            case LAINNYA -> "Lainnya";
        };
    }

    private String getStatusLabel(ReportStatus st) {
        return switch (st) {
            case PENDING -> "Menunggu (Pending)";
            case DIPROSES -> "Sedang Diproses";
            case SELESAI -> "Selesai (Final)";
            case DITOLAK -> "Ditolak";
        };
    }

    @PostMapping
    @Operation(summary = "Buat laporan baru",
               description = "Warga membuat laporan pengaduan. RT/RW otomatis diambil dari data user.")
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReportRequest request) {
        ReportResponse response = reportService.createReport(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Laporan berhasil dibuat", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail laporan",
               description = "Lihat detail laporan. Warga hanya bisa lihat miliknya, ADMIN_RT bisa lihat semua.")
    public ResponseEntity<ApiResponse<ReportResponse>> getReportById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        ReportResponse response = reportService.getReportById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Detail laporan berhasil diambil", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Laporan saya",
               description = "Lihat semua laporan milik user yang sedang login (paginasi)")
    public ResponseEntity<ApiResponse<PagedResponse<ReportResponse>>> getMyReports(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Nomor halaman (mulai dari 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Jumlah item per halaman") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<ReportResponse> response = reportService.getMyReports(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Laporan saya berhasil diambil", response));
    }

    @GetMapping
    @Operation(summary = "Semua laporan (ADMIN_RT)",
               description = "Lihat semua laporan dengan filter dan paginasi. Hanya untuk ADMIN_RT.")
    public ResponseEntity<ApiResponse<PagedResponse<ReportResponse>>> getAllReports(
            @Parameter(description = "Filter kategori") @RequestParam(required = false) ReportCategory category,
            @Parameter(description = "Filter status") @RequestParam(required = false) ReportStatus status,
            @Parameter(description = "Filter RT") @RequestParam(required = false) String rt,
            @Parameter(description = "Filter RW") @RequestParam(required = false) String rw,
            @Parameter(description = "Cari berdasarkan judul/deskripsi") @RequestParam(required = false) String keyword,
            @Parameter(description = "Nomor halaman (mulai dari 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Jumlah item per halaman") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<ReportResponse> response = reportService.getAllReports(category, status, rt, rw, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Semua laporan berhasil diambil", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit laporan",
               description = "Warga mengedit laporan miliknya. Hanya bisa saat status masih PENDING.")
    public ResponseEntity<ApiResponse<ReportResponse>> updateReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateReportRequest request) {
        ReportResponse response = reportService.updateReport(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Laporan berhasil diperbarui", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update status laporan (ADMIN_RT)",
               description = "ADMIN_RT mengubah status laporan: PENDING → DIPROSES → SELESAI / DITOLAK")
    public ResponseEntity<ApiResponse<ReportResponse>> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        ReportResponse response = reportService.updateStatus(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Status laporan berhasil diperbarui", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus laporan",
               description = "Warga menghapus laporan miliknya. Hanya bisa saat status masih PENDING.")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        reportService.deleteReport(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Laporan berhasil dihapus"));
    }
}
