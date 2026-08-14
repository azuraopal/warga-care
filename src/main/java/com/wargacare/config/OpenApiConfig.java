package com.wargacare.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI wargaCareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WargaCare API")
                        .description("""
                                **WargaCare — Sistem Pengaduan & Bantuan Warga RT/RW**
                                REST API untuk mengelola laporan warga, pengumuman RT/RW, jadwal kegiatan, dan monitoring lingkungan.
                                
                                ### Role Pengguna
                                - **WARGA**: Bisa membuat dan melihat laporan miliknya sendiri
                                - **ADMIN_RT**: Bisa mengelola semua laporan, membuat pengumuman dan jadwal kegiatan
                                - **RELAWAN**: Bisa melihat laporan yang ditugaskan dan update progres

                                ### Cara Penggunaan
                                1. Register akun baru via `POST /api/auth/register`
                                2. Login via `POST /api/auth/login` untuk mendapatkan JWT token
                                3. Klik tombol **Authorize** di atas dan masukkan token dengan format: `Bearer {token}`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WargaCare")
                                .email("admin@wargacare.id"))
                        .license(new License()
                                .name("MIT License")))

                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Masukkan JWT token yang didapat dari endpoint login")));
    }
}
