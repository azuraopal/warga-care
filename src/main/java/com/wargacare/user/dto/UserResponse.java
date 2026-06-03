package com.wargacare.user.dto;

import com.wargacare.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private String rt;
    private String rw;
    private String phone;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
