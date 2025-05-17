package com.jandi.band_backend.manage.entity;

import com.jandi.band_backend.promo.entity.PromoCommentReport;
import com.jandi.band_backend.promo.entity.PromoReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_reason")
@Getter
@Setter
@NoArgsConstructor
public class ReportReason {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_reason_id")
    private Integer id;
    
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @OneToMany(mappedBy = "reportReason")
    private List<PromoReport> promoReports = new ArrayList<>();
    
    @OneToMany(mappedBy = "reportReason")
    private List<PromoCommentReport> promoCommentReports = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
} 