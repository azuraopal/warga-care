package com.wargacare.announcement;

import com.wargacare.announcement.dto.AnnouncementResponse;
import com.wargacare.announcement.dto.CreateAnnouncementRequest;
import com.wargacare.announcement.dto.UpdateAnnouncementRequest;
import com.wargacare.common.PagedResponse;
import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import com.wargacare.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    private User admin;
    private Announcement announcement;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(2L)
                .fullName("Admin RT")
                .email("admin@test.com")
                .role(UserRole.ADMIN_RT)
                .build();

        announcement = Announcement.builder()
                .id(1L)
                .title("Kerja Bakti")
                .content("Hari minggu ada kerja bakti")
                .isPinned(false)
                .author(admin)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Buat pengumuman sukses")
    void create_success() {
        CreateAnnouncementRequest request = CreateAnnouncementRequest.builder()
                .title("Kerja Bakti")
                .content("Hari minggu ada kerja bakti")
                .isPinned(false)
                .build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);

        AnnouncementResponse response = announcementService.create("admin@test.com", request);

        assertThat(response.getTitle()).isEqualTo("Kerja Bakti");
        assertThat(response.getAuthor().getFullName()).isEqualTo("Admin RT");
    }

    @Test
    @DisplayName("Ambil semua pengumuman")
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Announcement> page = new PageImpl<>(List.of(announcement), pageable, 1);

        when(announcementRepository.findAllByOrderByIsPinnedDescCreatedAtDesc(pageable)).thenReturn(page);

        PagedResponse<AnnouncementResponse> response = announcementService.getAll(pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Update pengumuman sukses")
    void update_success() {
        UpdateAnnouncementRequest request = UpdateAnnouncementRequest.builder()
                .title("Kerja Bakti Rutin")
                .build();

        when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);

        AnnouncementResponse response = announcementService.update(1L, request);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Delete pengumuman sukses")
    void delete_success() {
        when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));

        announcementService.delete(1L);

        verify(announcementRepository).delete(announcement);
    }
}
