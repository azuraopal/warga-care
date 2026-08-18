package com.wargacare.kas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WargaMasterRepository extends JpaRepository<WargaMaster, Long> {
    List<WargaMaster> findByRtOrderByWargaNameAsc(String rt);
    Optional<WargaMaster> findByRtAndWargaName(String rt, String wargaName);
}
