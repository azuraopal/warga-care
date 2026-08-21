package com.wargacare.user.dto;

import com.wargacare.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    private String fullName;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, max = 100, message = "Password minimal 8 karakter")
    private String password;

    @NotNull(message = "Role tidak boleh kosong")
    private UserRole role;

    private String rt;
    private String rw;
    private String phone;
    private String address;
}
