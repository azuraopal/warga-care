package com.wargacare.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnnouncementRequest {

    @NotBlank(message = "Judul pengumuman wajib diisi")
    @Size(min = 5, max = 200, message = "Judul harus antara 5-200 karakter")
    private String title;

    @NotBlank(message = "Isi pengumuman wajib diisi")
    @Size(min = 10, max = 5000, message = "Isi pengumuman harus antara 10-5000 karakter")
    private String content;

    private Boolean isPinned;
}
