package com.wargacare.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    @EntityGraph(attributePaths = {"reporter"})
    Page<Report> findByReporterEmail(String email, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"reporter"})
    Page<Report> findAll(Specification<Report> spec, Pageable pageable);

    long countByStatus(ReportStatus status);

    long countByReporterId(Long reporterId);

    long countByReporterIdAndStatus(Long reporterId, ReportStatus status);
}
