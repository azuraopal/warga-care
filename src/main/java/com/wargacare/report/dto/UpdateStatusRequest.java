package com.wargacare.report.dto;

import com.wargacare.report.ReportStatus;
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
public class UpdateStatusRequest {

    @NotNull(message = "Status wajib diisi")
    private ReportStatus status;

    @Size(max = 500, message = "Catatan admin maksimal 500 karakter")
    private String adminNotes;

    @Size(max = 2000, message = "Bukti penyelesaian maksimal 2000 karakter")
    private String completionEvidence;
}
