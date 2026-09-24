package com.wargacare.kas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayIuranRequest {

    @NotBlank(message = "Nama warga harus diisi")
    @Size(max = 100, message = "Nama warga maksimal 100 karakter")
    private String wargaName;

    @Size(max = 100, message = "Alamat blok maksimal 100 karakter")
    private String blockAddress;

    @NotBlank(message = "Periode bulan harus diisi")
    @Size(max = 10, message = "Periode bulan maksimal 10 karakter")
    private String periodMonth;

    @DecimalMin(value = "0.01", message = "Nominal pembayaran harus lebih besar dari 0")
    private BigDecimal amount;

    @Size(max = 50, message = "Metode pembayaran maksimal 50 karakter")
    private String paymentMethod;
}
