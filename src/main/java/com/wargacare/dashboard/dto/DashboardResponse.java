package com.wargacare.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalWarga;
    private long totalLaporan;
    private long totalLaporanPending;
    private long totalPengumuman;
    private long totalKegiatan;
}
