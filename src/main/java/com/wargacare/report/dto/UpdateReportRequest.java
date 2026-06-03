package com.wargacare.report.dto;

import com.wargacare.report.ReportCategory;
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
}
