package com.wargacare.kas;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "warga_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WargaMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warga_name", nullable = false, length = 100)
    private String wargaName;

    @Column(name = "block_address", length = 100)
    private String blockAddress;

    @Column(nullable = false, length = 10)
    private String rt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WargaCategory category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (category == null) {
            category = WargaCategory.PEKERJA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
