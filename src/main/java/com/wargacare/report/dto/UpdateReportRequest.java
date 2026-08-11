package com.wargacare.report.dto;

import com.wargacare.report.ReportCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportRequest {

    @Size(min = 5, max = 200, message = "Judul harus antara 5-200 karakter")
    private String title;

    @Size(min = 10, max = 2000, message = "Deskripsi harus antara 10-2000 karakter")
    private String description;

    private ReportCategory category;

    @Size(max = 255, message = "Lokasi maksimal 255 karakter")
    private String location;

    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double longitude;
}
