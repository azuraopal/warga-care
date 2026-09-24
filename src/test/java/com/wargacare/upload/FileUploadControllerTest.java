package com.wargacare.upload;

import com.wargacare.common.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FileUploadController Unit Tests")
class FileUploadControllerTest {

    @Test
    @DisplayName("uploadFile() - Menghasilkan URL publik penuh ketika app.public-url dikonfigurasi")
    void uploadFile_WithPublicUrl_ReturnsFullUrl() {
        FileUploadController controller = new FileUploadController();
        ReflectionTestUtils.setField(controller, "publicUrl", "https://wargacare.trihech.cloud");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        ResponseEntity<ApiResponse<Map<String, String>>> response = controller.uploadFile(file, "events");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();

        String url = response.getBody().getData().get("url");
        assertThat(url).startsWith("https://wargacare.trihech.cloud/api/uploads/events/");
        assertThat(url).endsWith(".jpg");
    }

    @Test
    @DisplayName("uploadFile() - Menghasilkan URL relatif /uploads/ ketika app.public-url kosong (local dev)")
    void uploadFile_WithoutPublicUrl_ReturnsRelativeUrl() {
        FileUploadController controller = new FileUploadController();
        ReflectionTestUtils.setField(controller, "publicUrl", "");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "fake png content".getBytes()
        );

        ResponseEntity<ApiResponse<Map<String, String>>> response = controller.uploadFile(file, "reports");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        String url = response.getBody().getData().get("url");
        assertThat(url).startsWith("/uploads/reports/");
        assertThat(url).endsWith(".png");
    }

    @Test
    @DisplayName("uploadFile() - Gagal jika ekstensi file tidak diizinkan (misal .svg atau .exe)")
    void uploadFile_DisallowedExtension_ReturnsBadRequest() {
        FileUploadController controller = new FileUploadController();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vector.svg",
                "image/svg+xml",
                "<svg></svg>".getBytes()
        );

        ResponseEntity<ApiResponse<Map<String, String>>> response = controller.uploadFile(file, "general");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
    }
}
