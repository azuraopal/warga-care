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
    private String wargaName;
    private String blockAddress;
    private String periodWeek;
    private BigDecimal amount;
    private String paymentMethod;
}
