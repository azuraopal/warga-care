package com.wargacare.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=";

    private final RestTemplate restTemplate;

    public ChatService() {
        this.restTemplate = new RestTemplate();
    }

    public String getChatResponse(String userMessage) {
        String url = GEMINI_API_URL + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> systemInstruction = new HashMap<>();
        Map<String, String> sysPart = new HashMap<>();
        sysPart.put("text", "Anda adalah asisten virtual resmi untuk aplikasi WargaCare (fokus di Indonesia). Tugas Anda HANYA menjawab pertanyaan seputar Karang Taruna, definisi RT, RW, serta ruang lingkup tata kelola administratif di tingkat Desa. Jika pengguna bertanya di luar topik tersebut (misalnya cuaca, teknologi, resep masakan, dll), tolak secara sopan, ringkas, ramah, dan arahkan mereka kembali ke topik seputar lingkungan RT/RW/Desa/Karang Taruna.");
        systemInstruction.put("parts", sysPart);
        requestBody.put("system_instruction", systemInstruction);

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentItem = new HashMap<>();
        contentItem.put("role", "user");
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> textPart = new HashMap<>();
        textPart.put("text", userMessage);
        parts.add(textPart);
        contentItem.put("parts", parts);
        contents.add(contentItem);
        requestBody.put("contents", contents);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                    List<Map<String, Object>> resParts = (List<Map<String, Object>>) content.get("parts");
                    if (!resParts.isEmpty()) {
                        return (String) resParts.get(0).get("text");
                    }
                }
            }
            return "Maaf, saya tidak dapat merespons saat ini.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Maaf, terjadi kesalahan saat menghubungi layanan AI kami.";
        }
    }
}
