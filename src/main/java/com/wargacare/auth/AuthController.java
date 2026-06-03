package com.wargacare.auth;

import com.wargacare.auth.dto.AuthResponse;
import com.wargacare.auth.dto.LoginRequest;
import com.wargacare.auth.dto.RegisterRequest;
import com.wargacare.common.ApiResponse;
import com.wargacare.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoint untuk register, login, dan informasi pengguna")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register pengguna baru",
               description = "Mendaftarkan pengguna baru dengan role WARGA secara default")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registrasi berhasil. Selamat datang di WargaCare!", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login pengguna",
               description = "Autentikasi dengan email dan password, mengembalikan JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Informasi pengguna saat ini",
               description = "Mendapatkan data pengguna yang sedang terautentikasi",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Data pengguna berhasil diambil", user));
    }
}
