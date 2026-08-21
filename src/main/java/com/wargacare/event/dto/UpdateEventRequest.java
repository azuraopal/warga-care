package com.wargacare.event.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @Size(min = 5, max = 200, message = "Judul harus antara 5-200 karakter")
    private String title;

    @Size(min = 10, max = 5000, message = "Deskripsi harus antara 10-5000 karakter")
    private String description;

    @FutureOrPresent(message = "Tanggal kegiatan harus di masa depan atau hari ini")
    private LocalDateTime eventDate;

    @Size(max = 255, message = "Lokasi maksimal 255 karakter")
    private String location;

    @Size(max = 500, message = "URL Gambar maksimal 500 karakter")
    private String imageUrl;
}
