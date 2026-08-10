package com.wargacare.user;

import com.wargacare.common.ApiResponse;
import com.wargacare.common.PagedResponse;
import com.wargacare.user.dto.UpdateRoleRequest;
import com.wargacare.user.dto.UpdateUserStatusRequest;
import com.wargacare.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoint manajemen pengguna (ADMIN_RT)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN_RT')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Daftar pengguna (ADMIN_RT)")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @Parameter(description = "Filter role (WARGA/ADMIN_RT)") @RequestParam(required = false) UserRole role,
            @Parameter(description = "Cari berdasarkan nama/email/rt") @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<UserResponse> response = userService.getAllUsers(role, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Daftar pengguna berhasil diambil", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail pengguna")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Detail pengguna berhasil diambil", response));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Ubah role pengguna (ADMIN_RT)")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        UserResponse response = userService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Role pengguna berhasil diperbarui", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Ubah status aktif pengguna (ADMIN_RT)")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        UserResponse response = userService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status pengguna berhasil diperbarui", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus pengguna (ADMIN_RT)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Pengguna berhasil dihapus"));
    }
}
