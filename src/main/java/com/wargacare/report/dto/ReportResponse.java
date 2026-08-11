package com.wargacare.report.dto;

import com.wargacare.report.ReportCategory;
import com.wargacare.report.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private String title;
    private String description;
    private ReportCategory category;
    private ReportStatus status;
    private String location;
    private Double latitude;
    private Double longitude;
    private String photoEvidence;
    private String rt;
    private String rw;
    private String adminNotes;
    private String completionEvidence;
    private ReporterInfo reporter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReporterInfo {
        private Long id;
        private String fullName;
        private String email;
        private String rt;
        private String rw;
    }
}
