package com.jandi.band_backend.club.repository;

import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubUserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findById(Long id);
}
