package com.jandi.band_backend.univ.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "지역 엔티티")
@Entity
@Table(name = "region")
@Getter
@Setter
@NoArgsConstructor
public class Region {
    
    @Schema(description = "지역 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Integer id;
    
    @Schema(description = "지역 코드", example = "SEOUL")
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    
    @Schema(description = "지역 이름", example = "서울")
    @Column(name = "name", nullable = false, length = 50)
    private String name;
} 