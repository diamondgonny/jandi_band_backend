package com.jandi.band_backend.user.repository;

import com.jandi.band_backend.user.entity.UserTimetable;
import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTimetableRepository extends JpaRepository<UserTimetable, Long> {
    List<UserTimetable> findByUserAndDeletedAtIsNull(Users user);
}
