package com.wargacare.kas;

import java.math.BigDecimal;

public enum WargaCategory {
    PELAJAR(new BigDecimal("2000.00"), "Pelajar / Sekolah"),
    PEKERJA(new BigDecimal("5000.00"), "Sudah Bekerja");

    private final BigDecimal weeklyDuesRate;
    private final String label;

    WargaCategory(BigDecimal weeklyDuesRate, String label) {
        this.weeklyDuesRate = weeklyDuesRate;
        this.label = label;
    }

    public BigDecimal getWeeklyDuesRate() {
        return weeklyDuesRate;
    }

    public String getLabel() {
        return label;
    }
}
