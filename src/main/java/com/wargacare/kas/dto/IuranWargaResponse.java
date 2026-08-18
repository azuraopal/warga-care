package com.wargacare.kas.dto;

import com.wargacare.kas.IuranWarga;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class IuranWargaResponse {

    private Long id;
    private String wargaName;
    private String blockAddress;
    private String rt;
    private String periodMonth;
    private BigDecimal amount;
    private Boolean isPaid;
    private LocalDate paidDate;
    private String paymentMethod;
    private String recordedBy;

    public static IuranWargaResponse fromEntity(IuranWarga entity) {
        return IuranWargaResponse.builder()
                .id(entity.getId())
                .wargaName(entity.getWargaName())
                .blockAddress(entity.getBlockAddress())
                .rt(entity.getRt())
                .periodMonth(entity.getPeriodMonth())
                .amount(entity.getAmount())
                .isPaid(entity.getIsPaid())
                .paidDate(entity.getPaidDate())
                .paymentMethod(entity.getPaymentMethod())
                .recordedBy(entity.getRecordedBy())
                .build();
    }
}
