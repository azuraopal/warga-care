package com.wargacare.kas.dto;

import com.wargacare.kas.WargaCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterWargaRequest {
    @NotBlank(message = "Nama warga wajib diisi")
    private String wargaName;
    private String blockAddress;
    private WargaCategory category; // PELAJAR or PEKERJA
}
