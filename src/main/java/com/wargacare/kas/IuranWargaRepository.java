package com.wargacare.kas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IuranWargaRepository extends JpaRepository<IuranWarga, Long> {
    List<IuranWarga> findByRtOrderByWargaNameAsc(String rt);
    List<IuranWarga> findByRtAndPeriodWeek(String rt, String periodWeek);
    Optional<IuranWarga> findByRtAndWargaNameAndPeriodMonth(String rt, String wargaName, String periodMonth);
    Optional<IuranWarga> findByRtAndWargaNameAndPeriodWeek(String rt, String wargaName, String periodWeek);
    Optional<IuranWarga> findByRtAndWargaMasterIdAndPeriodWeek(String rt, Long wargaMasterId, String periodWeek);
    List<IuranWarga> findByRtAndWargaMasterId(String rt, Long wargaMasterId);
}
