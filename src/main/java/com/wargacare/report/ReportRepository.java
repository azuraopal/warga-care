package com.wargacare.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    Page<Report> findByReporterEmail(String email, Pageable pageable);

    long countByStatus(ReportStatus status);

    long countByReporterId(Long reporterId);

    long countByReporterIdAndStatus(Long reporterId, ReportStatus status);
}
