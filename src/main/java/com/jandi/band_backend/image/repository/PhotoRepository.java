package com.jandi.band_backend.image.repository;

import com.jandi.band_backend.image.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
