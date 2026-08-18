package com.wargacare.kas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface KasTransactionRepository extends JpaRepository<KasTransaction, Long> {

    List<KasTransaction> findByRtOrderByDateDescCreatedAtDesc(String rt);

    @Query("SELECT k FROM KasTransaction k WHERE k.rt = :rt " +
           "AND (:type IS NULL OR k.type = :type) " +
           "AND (:category IS NULL OR k.category = :category) " +
           "AND (:search IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(k.notes) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY k.date DESC, k.createdAt DESC")
    List<KasTransaction> filterTransactions(
            @Param("rt") String rt,
            @Param("type") KasType type,
            @Param("category") String category,
            @Param("search") String search
    );

    @Query("SELECT COALESCE(SUM(k.amount), 0) FROM KasTransaction k WHERE k.rt = :rt AND k.type = :type")
    BigDecimal sumAmountByRtAndType(@Param("rt") String rt, @Param("type") KasType type);
}
