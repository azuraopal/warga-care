package com.wargacare.auth.dto;

import com.wargacare.user.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama lengkap harus antara 3-100 karakter")
    private String fullName;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @Size(max = 150, message = "Email maksimal 150 karakter")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, max = 100, message = "Password harus minimal 8 karakter")
    private String password;

    @Size(max = 10, message = "RT maksimal 10 karakter")
    private String rt;

    @Size(max = 10, message = "RW maksimal 10 karakter")
    private String rw;

    @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
    private String phone;

    private String address;

    private UserRole role;

    public RegisterRequest(String fullName, String email, String password, String rt, String rw, String phone, String address) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.rt = rt;
        this.rw = rw;
        this.phone = phone;
        this.address = address;
    }
}

