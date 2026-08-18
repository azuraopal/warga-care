package com.wargacare.kas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayIuranRequest {

    @NotBlank(message = "Nama warga harus diisi")
    private String wargaName;

    private String blockAddress;

    @NotBlank(message = "Periode bulan harus diisi")
    private String periodMonth; // format YYYY-MM e.g. 2026-08

    private BigDecimal amount;
    private String paymentMethod; // e.g. 'Tunai' or 'Transfer'
}
