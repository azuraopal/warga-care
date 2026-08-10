package com.wargacare.user;

import com.wargacare.common.PagedResponse;
import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.user.dto.UpdateRoleRequest;
import com.wargacare.user.dto.UpdateUserStatusRequest;
import com.wargacare.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(UserRole role, String keyword, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim().toLowerCase() : null;
        Page<User> usersPage = userRepository.findAllFiltered(role, cleanKeyword, pageable);

        return PagedResponse.<UserResponse>builder()
                .content(usersPage.getContent().stream().map(this::mapToUserResponse).toList())
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .last(usersPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan dengan id: " + id));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateRole(Long id, UpdateRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan dengan id: " + id));

        user.setRole(request.getRole());
        User updated = userRepository.save(user);
        return mapToUserResponse(updated);
    }

    @Transactional
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan dengan id: " + id));

        user.setIsActive(request.getIsActive());
        User updated = userRepository.save(user);
        return mapToUserResponse(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan dengan id: " + id));
        userRepository.delete(user);
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .rt(user.getRt())
                .rw(user.getRw())
                .phone(user.getPhone())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
