package com.wargacare.kas.dto;

import com.wargacare.kas.WargaCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyIuranStatusResponse {
    private Long wargaMasterId;
    private String wargaName;
    private String blockAddress;
    private String rt;
    private WargaCategory category;
    private String categoryLabel;
    private BigDecimal weeklyDuesRate;
    private String periodWeek;
    private Boolean isPaid; // true = LUNAS, false = MENUNGGAK
    private LocalDate paidDate;
    private String paymentMethod;
    private String recordedBy;
    private Integer totalArrearsWeeks; // count of unpaid weeks for this resident
    private BigDecimal totalArrearsAmount;
}
