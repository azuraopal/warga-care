package com.wargacare.kas.dto;

import com.wargacare.kas.KasTransaction;
import com.wargacare.kas.KasType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class KasTransactionResponse {

    private Long id;
    private String rt;
    private KasType type;
    private String title;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String recordedBy;
    private String notes;
    private String proofUrl;
    private LocalDateTime createdAt;

    public static KasTransactionResponse fromEntity(KasTransaction entity) {
        return KasTransactionResponse.builder()
                .id(entity.getId())
                .rt(entity.getRt())
                .type(entity.getType())
                .title(entity.getTitle())
                .amount(entity.getAmount())
                .category(entity.getCategory())
                .date(entity.getDate())
                .recordedBy(entity.getRecordedBy())
                .notes(entity.getNotes())
                .proofUrl(entity.getProofUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
