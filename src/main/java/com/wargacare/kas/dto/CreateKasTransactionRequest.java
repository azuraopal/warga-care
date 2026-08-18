package com.wargacare.kas.dto;

import com.wargacare.kas.KasType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateKasTransactionRequest {

    private String rt;

    @NotNull(message = "Jenis transaksi (INCOME/EXPENSE) harus diisi")
    private KasType type;

    @NotBlank(message = "Judul transaksi harus diisi")
    private String title;

    @NotNull(message = "Jumlah nominal harus diisi")
    @DecimalMin(value = "0.01", message = "Nominal harus lebih besar dari 0")
    private BigDecimal amount;

    @NotBlank(message = "Kategori transaksi harus diisi")
    private String category;

    @NotNull(message = "Tanggal transaksi harus diisi")
    private LocalDate date;

    private String notes;
    private String proofUrl;
}
