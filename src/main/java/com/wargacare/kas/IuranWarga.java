package com.wargacare.kas;

import com.wargacare.user.User;
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
@Table(name = "iuran_warga")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IuranWarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "warga_master_id")
    private Long wargaMasterId;

    @Column(name = "warga_name", nullable = false, length = 100)
    private String wargaName;

    @Column(name = "block_address", length = 100)
    private String blockAddress;

    @Column(name = "rt", nullable = false, length = 10)
    private String rt;

    @Column(name = "period_month", nullable = false, length = 10)
    private String periodMonth;

    @Column(name = "period_week", length = 10)
    private String periodWeek; // e.g. "2026-W33"

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal amount = new BigDecimal("50000.00");

    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "recorded_by", length = 150)
    private String recordedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
