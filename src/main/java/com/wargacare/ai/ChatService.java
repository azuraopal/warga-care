package com.wargacare.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=";

    @Value("${gemini.api.key}")
    private String geminiApiKey;

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
            
            if (responseBody == null) {
                log.warn("Empty response from Gemini API");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            if (!responseBody.containsKey("candidates")) {
                log.warn("No candidates in Gemini API response");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            Object candidatesObj = responseBody.get("candidates");
            if (!(candidatesObj instanceof List)) {
                log.warn("Candidates is not a list in Gemini API response");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) candidatesObj;
            
            if (candidates.isEmpty()) {
                log.warn("Candidates list is empty in Gemini API response");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            Map<String, Object> firstCandidate = candidates.get(0);
            if (firstCandidate == null) {
                log.warn("First candidate is null");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            Object contentObj = firstCandidate.get("content");
            if (contentObj == null) {
                log.warn("Content is null in first candidate");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            if (!(contentObj instanceof Map)) {
                log.warn("Content is not a map in Gemini API response");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) contentObj;
            
            Object partsObj = content.get("parts");
            if (partsObj == null) {
                log.warn("Parts is null in content");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            if (!(partsObj instanceof List)) {
                log.warn("Parts is not a list in Gemini API response");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resParts = (List<Map<String, Object>>) partsObj;
            
            if (resParts.isEmpty()) {
                log.warn("Parts list is empty");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            Map<String, Object> textMap = resParts.get(0);
            if (textMap == null || !textMap.containsKey("text")) {
                log.warn("Text not found in first part");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            Object textObj = textMap.get("text");
            if (!(textObj instanceof String)) {
                log.warn("Text is not a string");
                return "Maaf, saya tidak dapat merespons saat ini.";
            }
            
            return (String) textObj;
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            return "Maaf, terjadi kesalahan saat menghubungi layanan AI kami.";
        }
    }
}
