package com.wargacare.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = "Pesan tidak boleh kosong")
    private String message;
}
