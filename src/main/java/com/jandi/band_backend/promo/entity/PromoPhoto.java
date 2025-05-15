package com.jandi.band_backend.promo.entity;

import com.jandi.band_backend.image.entity.Photo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "promo_photo")
@Getter
@Setter
@NoArgsConstructor
public class PromoPhoto {
    
    @Id
    @Column(name = "photo_id")
    private Integer id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "photo_id")
    private Photo photo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
} 