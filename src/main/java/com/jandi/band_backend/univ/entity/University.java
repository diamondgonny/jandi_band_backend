package com.jandi.band_backend.univ.entity;

import com.jandi.band_backend.club.entity.ClubUniversity;
import com.jandi.band_backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "university")
@Getter
@Setter
@NoArgsConstructor
public class University {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "university_id")
    private Integer id;
    
    @Column(name = "university_code", nullable = false, unique = true, columnDefinition = "char(7)")
    private String universityCode;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @OneToMany(mappedBy = "university")
    private List<Users> users = new ArrayList<>();
    
    @OneToMany(mappedBy = "university")
    private List<ClubUniversity> clubUniversities = new ArrayList<>();
} 