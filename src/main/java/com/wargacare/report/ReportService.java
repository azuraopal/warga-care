package com.wargacare.report;

import com.wargacare.common.PagedResponse;
import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.report.dto.*;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import com.wargacare.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReportResponse createReport(String email, CreateReportRequest request) {
        User reporter = findUserByEmail(email);

        Report report = Report.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .location(request.getLocation())
                .rt(reporter.getRt())
                .rw(reporter.getRw())
                .reporter(reporter)
                .build();

        Report saved = reportRepository.save(report);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long id, String email) {
        Report report = findReportById(id);
        User user = findUserByEmail(email);

        if (!isOwner(report, user) && !isAdmin(user)) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke laporan ini");
        }

        return mapToResponse(report);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ReportResponse> getMyReports(String email, Pageable pageable) {
        Page<Report> page = reportRepository.findByReporterEmail(email, pageable);
        return mapToPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ReportResponse> getAllReports(
            ReportCategory category,
            ReportStatus status,
            String rt,
            String rw,
            String keyword,
            Pageable pageable) {

        Specification<Report> spec = ReportSpecification.withFilters(category, status, rt, rw, keyword);
        Page<Report> page = reportRepository.findAll(spec, pageable);
        return mapToPagedResponse(page);
    }

    @Transactional
    public ReportResponse updateReport(Long id, String email, UpdateReportRequest request) {
        Report report = findReportById(id);
        User user = findUserByEmail(email);

        if (!isOwner(report, user)) {
            throw new AccessDeniedException("Anda hanya bisa mengedit laporan milik sendiri");
        }

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("Laporan hanya bisa diedit saat status masih PENDING");
        }

        if (request.getTitle() != null) {
            report.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            report.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            report.setCategory(request.getCategory());
        }
        if (request.getLocation() != null) {
            report.setLocation(request.getLocation());
        }

        Report updated = reportRepository.save(report);
        return mapToResponse(updated);
    }

    @Transactional
    public ReportResponse updateStatus(Long id, String email, UpdateStatusRequest request) {
        Report report = findReportById(id);
        User admin = findUserByEmail(email);

        if (!isAdmin(admin)) {
            throw new AccessDeniedException("Hanya ADMIN_RT yang dapat mengubah status laporan");
        }

        report.setStatus(request.getStatus());
        if (request.getAdminNotes() != null) {
            report.setAdminNotes(request.getAdminNotes());
        }

        Report updated = reportRepository.save(report);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteReport(Long id, String email) {
        Report report = findReportById(id);
        User user = findUserByEmail(email);

        if (!isOwner(report, user)) {
            throw new AccessDeniedException("Anda hanya bisa menghapus laporan milik sendiri");
        }

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("Laporan hanya bisa dihapus saat status masih PENDING");
        }

        reportRepository.delete(report);
    }

    private Report findReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laporan dengan ID " + id + " tidak ditemukan"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private boolean isOwner(Report report, User user) {
        return report.getReporter().getId().equals(user.getId());
    }

    private boolean isAdmin(User user) {
        return user.getRole() == UserRole.ADMIN_RT;
    }

    private ReportResponse mapToResponse(Report report) {
        User reporter = report.getReporter();

        ReportResponse.ReporterInfo reporterInfo = ReportResponse.ReporterInfo.builder()
                .id(reporter.getId())
                .fullName(reporter.getFullName())
                .email(reporter.getEmail())
                .rt(reporter.getRt())
                .rw(reporter.getRw())
                .build();

        return ReportResponse.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .category(report.getCategory())
                .status(report.getStatus())
                .location(report.getLocation())
                .rt(report.getRt())
                .rw(report.getRw())
                .adminNotes(report.getAdminNotes())
                .reporter(reporterInfo)
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }

    private PagedResponse<ReportResponse> mapToPagedResponse(Page<Report> page) {
        return PagedResponse.<ReportResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
