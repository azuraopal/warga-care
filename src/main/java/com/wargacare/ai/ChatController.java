package com.wargacare.ai;

import com.wargacare.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> chat(@Valid @RequestBody ChatRequest request) {
        String responseText = chatService.getChatResponse(request.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("reply", responseText);
        return ResponseEntity.ok(ApiResponse.success("Berhasil memproses percakapan", response));
    }
}
