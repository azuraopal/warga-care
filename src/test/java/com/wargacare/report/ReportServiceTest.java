package com.wargacare.report;

import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.report.dto.*;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService reportService;

    private User warga;
    private User admin;
    private Report pendingReport;

    @BeforeEach
    void setUp() {
        warga = User.builder()
                .id(1L)
                .fullName("Budi Warga")
                .email("budi@test.com")
                .password("hashed")
                .role(UserRole.WARGA)
                .rt("001")
                .rw("005")
                .build();

        admin = User.builder()
                .id(2L)
                .fullName("Admin RT")
                .email("admin@test.com")
                .password("hashed")
                .role(UserRole.ADMIN_RT)
                .rt("001")
                .rw("005")
                .build();

        pendingReport = Report.builder()
                .id(1L)
                .title("Jalan Rusak di Gang 3")
                .description("Jalan berlubang besar, berbahaya untuk pengendara motor")
                .category(ReportCategory.JALAN_RUSAK)
                .status(ReportStatus.PENDING)
                .location("Gang 3 RT 001 RW 005")
                .rt("001")
                .rw("005")
                .reporter(warga)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Buat laporan berhasil")
    void createReport_success() {
        CreateReportRequest request = CreateReportRequest.builder()
                .title("Jalan Rusak di Gang 3")
                .description("Jalan berlubang besar, berbahaya untuk pengendara motor")
                .category(ReportCategory.JALAN_RUSAK)
                .location("Gang 3 RT 001 RW 005")
                .build();

        when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(warga));
        when(reportRepository.save(any(Report.class))).thenReturn(pendingReport);

        ReportResponse response = reportService.createReport("budi@test.com", request);

        assertThat(response.getTitle()).isEqualTo("Jalan Rusak di Gang 3");
        assertThat(response.getCategory()).isEqualTo(ReportCategory.JALAN_RUSAK);
        assertThat(response.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(response.getRt()).isEqualTo("001");
        assertThat(response.getReporter().getFullName()).isEqualTo("Budi Warga");
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Buat laporan gagal - user tidak ditemukan")
    void createReport_userNotFound() {
        CreateReportRequest request = CreateReportRequest.builder()
                .title("Test")
                .description("Test deskripsi panjang cukup")
                .category(ReportCategory.SAMPAH)
                .build();

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.createReport("unknown@test.com", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User tidak ditemukan");
    }

    @Test
    @DisplayName("Lihat detail laporan - owner berhasil")
    void getReportById_ownerSuccess() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(warga));

        ReportResponse response = reportService.getReportById(1L, "budi@test.com");

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Jalan Rusak di Gang 3");
    }

    @Test
    @DisplayName("Lihat detail laporan - admin berhasil melihat laporan orang lain")
    void getReportById_adminCanViewAny() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        ReportResponse response = reportService.getReportById(1L, "admin@test.com");

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Lihat detail laporan - bukan owner dan bukan admin ditolak")
    void getReportById_notOwnerNotAdmin_denied() {
        User otherWarga = User.builder()
                .id(3L)
                .email("other@test.com")
                .role(UserRole.WARGA)
                .build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherWarga));

        assertThatThrownBy(() -> reportService.getReportById(1L, "other@test.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("tidak memiliki akses");
    }

    @Test
    @DisplayName("Laporan tidak ditemukan")
    void getReportById_notFound() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.getReportById(999L, "budi@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("tidak ditemukan");
    }

    @Test
    @DisplayName("Lihat laporan saya - paginasi")
    void getMyReports_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Report> page = new PageImpl<>(List.of(pendingReport), pageable, 1);

        when(reportRepository.findByReporterEmail("budi@test.com", pageable)).thenReturn(page);

        var response = reportService.getMyReports("budi@test.com", pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(0);
    }

    @Test
    @DisplayName("Update laporan berhasil - status PENDING")
    void updateReport_success() {
        UpdateReportRequest request = UpdateReportRequest.builder()
                .title("Jalan Rusak Parah di Gang 3")
                .build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(warga));
        when(reportRepository.save(any(Report.class))).thenReturn(pendingReport);

        ReportResponse response = reportService.updateReport(1L, "budi@test.com", request);

        assertThat(response).isNotNull();
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Update laporan gagal - status bukan PENDING")
    void updateReport_notPending() {
        pendingReport.setStatus(ReportStatus.DIPROSES);

        UpdateReportRequest request = UpdateReportRequest.builder()
                .title("Update judul")
                .build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(warga));

        assertThatThrownBy(() -> reportService.updateReport(1L, "budi@test.com", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING");
    }

    @Test
    @DisplayName("Update laporan gagal - bukan owner")
    void updateReport_notOwner() {
        User otherWarga = User.builder()
                .id(3L)
                .email("other@test.com")
                .role(UserRole.WARGA)
                .build();

        UpdateReportRequest request = UpdateReportRequest.builder()
                .title("Update judul")
                .build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherWarga));

        assertThatThrownBy(() -> reportService.updateReport(1L, "other@test.com", request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("milik sendiri");
    }

    @Test
    @DisplayName("Update status berhasil - ADMIN_RT")
    void updateStatus_adminSuccess() {
        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(ReportStatus.DIPROSES)
                .adminNotes("Sudah ditinjau, akan diperbaiki minggu depan")
                .build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(reportRepository.save(any(Report.class))).thenReturn(pendingReport);

        ReportResponse response = reportService.updateStatus(1L, "admin@test.com", request);

        assertThat(response).isNotNull();
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Update status gagal - bukan ADMIN_RT")
    void updateStatus_notAdmin() {
        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(ReportStatus.DIPROSES)
                .build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(warga));

        assertThatThrownBy(() -> reportService.updateStatus(1L, "budi@test.com", request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("ADMIN_RT");
    }

    @Test
    @DisplayName("Hapus laporan berhasil - owner, status PENDING")
    void deleteReport_success() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(warga));

        reportService.deleteReport(1L, "budi@test.com");

        verify(reportRepository).delete(pendingReport);
    }

    @Test
    @DisplayName("Hapus laporan gagal - status bukan PENDING")
    void deleteReport_notPending() {
        pendingReport.setStatus(ReportStatus.SELESAI);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(pendingReport));
        when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(warga));

        assertThatThrownBy(() -> reportService.deleteReport(1L, "budi@test.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING");
    }
}
