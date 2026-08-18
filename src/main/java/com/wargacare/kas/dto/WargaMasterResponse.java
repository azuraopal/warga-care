package com.wargacare.kas.dto;

import com.wargacare.kas.WargaCategory;
import com.wargacare.kas.WargaMaster;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WargaMasterResponse {
    private Long id;
    private String wargaName;
    private String blockAddress;
    private String rt;
    private WargaCategory category;
    private String categoryLabel;
    private BigDecimal weeklyDuesRate;

    public static WargaMasterResponse fromEntity(WargaMaster entity) {
        return WargaMasterResponse.builder()
                .id(entity.getId())
                .wargaName(entity.getWargaName())
                .blockAddress(entity.getBlockAddress())
                .rt(entity.getRt())
                .category(entity.getCategory())
                .categoryLabel(entity.getCategory() != null ? entity.getCategory().getLabel() : "Sudah Bekerja")
                .weeklyDuesRate(entity.getCategory() != null ? entity.getCategory().getWeeklyDuesRate() : new BigDecimal("5000.00"))
                .build();
    }
}
