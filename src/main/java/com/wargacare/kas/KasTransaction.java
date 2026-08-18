package com.wargacare.kas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kas_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KasTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rt", nullable = false, length = 10)
    private String rt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private KasType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "recorded_by", length = 150)
    private String recordedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "proof_url", length = 500)
    private String proofUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
