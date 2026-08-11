package com.wargacare.dashboard;

import com.wargacare.announcement.AnnouncementRepository;
import com.wargacare.dashboard.dto.DashboardResponse;
import com.wargacare.event.EventRepository;
import com.wargacare.report.ReportRepository;
import com.wargacare.report.ReportStatus;
import com.wargacare.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final AnnouncementRepository announcementRepository;
    private final EventRepository eventRepository;

    public DashboardService(
            UserRepository userRepository,
            ReportRepository reportRepository,
            AnnouncementRepository announcementRepository,
            EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.announcementRepository = announcementRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStats() {
        return DashboardResponse.builder()
                .totalWarga(userRepository.count())
                .totalLaporan(reportRepository.count())
                .totalLaporanPending(reportRepository.countByStatus(ReportStatus.PENDING))
                .totalLaporanSelesai(reportRepository.countByStatus(ReportStatus.SELESAI))
                .totalPengumuman(announcementRepository.count())
                .totalKegiatan(eventRepository.count())
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStatsForUserEmail(String email) {
        com.wargacare.user.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getDashboardStatsForUser(user.getId());
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStatsForUser(Long userId) {
        return DashboardResponse.builder()
                .totalWarga(userRepository.count())
                .totalLaporan(reportRepository.countByReporterId(userId))
                .totalLaporanPending(reportRepository.countByReporterIdAndStatus(userId, ReportStatus.PENDING))
                .totalLaporanSelesai(reportRepository.countByReporterIdAndStatus(userId, ReportStatus.SELESAI))
                .totalPengumuman(announcementRepository.count())
                .totalKegiatan(eventRepository.count())
                .build();
    }
}
