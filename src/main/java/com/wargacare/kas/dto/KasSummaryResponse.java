package com.wargacare.kas.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class KasSummaryResponse {

    private String rt;
    private BigDecimal currentBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal monthIncome;
    private BigDecimal monthExpense;
    private long transactionCount;
}
