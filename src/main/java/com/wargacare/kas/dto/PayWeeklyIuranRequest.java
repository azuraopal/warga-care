package com.wargacare.kas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayWeeklyIuranRequest {
    private Long wargaMasterId;
    @NotBlank(message = "Nama warga wajib diisi")
    private String wargaName;
    private String blockAddress;
    private String periodWeek; // e.g. "2026-W33"
    private BigDecimal amount; // null to auto-fill based on WargaCategory
    private String paymentMethod;
}
