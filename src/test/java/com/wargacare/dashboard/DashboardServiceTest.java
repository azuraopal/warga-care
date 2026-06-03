package com.wargacare.dashboard;

import com.wargacare.announcement.AnnouncementRepository;
import com.wargacare.dashboard.dto.DashboardResponse;
import com.wargacare.event.EventRepository;
import com.wargacare.report.ReportRepository;
import com.wargacare.report.ReportStatus;
import com.wargacare.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Ambil statistik dashboard sukses")
    void getDashboardStats_success() {
        when(userRepository.count()).thenReturn(150L);
        when(reportRepository.count()).thenReturn(20L);
        when(reportRepository.countByStatus(ReportStatus.PENDING)).thenReturn(5L);
        when(announcementRepository.count()).thenReturn(10L);
        when(eventRepository.count()).thenReturn(2L);

        DashboardResponse response = dashboardService.getDashboardStats();

        assertThat(response.getTotalWarga()).isEqualTo(150L);
        assertThat(response.getTotalLaporan()).isEqualTo(20L);
        assertThat(response.getTotalLaporanPending()).isEqualTo(5L);
        assertThat(response.getTotalPengumuman()).isEqualTo(10L);
        assertThat(response.getTotalKegiatan()).isEqualTo(2L);
    }
}
