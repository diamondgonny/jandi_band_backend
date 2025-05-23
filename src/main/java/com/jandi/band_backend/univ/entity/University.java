package com.jandi.band_backend.univ.entity;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "대학교 엔티티")
@Entity
@Table(name = "university")
@Getter
@Setter
@NoArgsConstructor
public class University {

    @Schema(description = "대학 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "university_id")
    private Integer id;

    @Schema(description = "대학 코드", example = "1001234")
    @Column(name = "university_code", nullable = false, unique = true, columnDefinition = "char(7)")
    private String universityCode;

    @Schema(description = "대학 명칭", example = "서울대학교")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Schema(description = "소재지 코드")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Schema(description = "주소", example = "서울특별시 관악구 관악로 1")
    @Column(name = "address", length = 255)
    private String address;

    @Schema(description = "소속 사용자 목록")
    @OneToMany(mappedBy = "university")
    private List<Users> users = new ArrayList<>();

    @Schema(description = "소속 동아리 목록")
    @OneToMany(mappedBy = "university")
    private List<Club> clubs = new ArrayList<>();
}
