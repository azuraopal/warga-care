package com.wargacare.dashboard;

import com.wargacare.common.ApiResponse;
import com.wargacare.dashboard.dto.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoint untuk statistik dashboard ADMIN_RT")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Statistik Dashboard (ADMIN_RT)",
               description = "Mendapatkan total warga, laporan, laporan pending, pengumuman, dan kegiatan")
    public ResponseEntity<ApiResponse<DashboardResponse>> getStats() {
        DashboardResponse response = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Statistik berhasil diambil", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Statistik Dashboard (Warga)",
               description = "Mendapatkan statistik dashboard untuk warga yang sedang login")
    public ResponseEntity<ApiResponse<DashboardResponse>> getMyStats(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        DashboardResponse response = dashboardService.getDashboardStatsForUserEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Statistik berhasil diambil", response));
    }
}
