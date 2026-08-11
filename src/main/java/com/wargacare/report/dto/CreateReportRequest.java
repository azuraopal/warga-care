package com.wargacare.report.dto;

import com.wargacare.report.ReportCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {

    @NotBlank(message = "Judul laporan wajib diisi")
    @Size(min = 5, max = 200, message = "Judul harus antara 5-200 karakter")
    private String title;

    @NotBlank(message = "Deskripsi laporan wajib diisi")
    @Size(min = 10, max = 2000, message = "Deskripsi harus antara 10-2000 karakter")
    private String description;

    @NotNull(message = "Kategori laporan wajib diisi")
    private ReportCategory category;

    @Size(max = 255, message = "Lokasi maksimal 255 karakter")
    private String location;

    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double longitude;

    @Size(max = 500, message = "URL foto bukti maksimal 500 karakter")
    private String photoEvidence;
}
