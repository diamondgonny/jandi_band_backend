package com.jandi.band_backend.user.repository;

import com.jandi.band_backend.user.entity.UserTimetable;
import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTimetableRepository extends JpaRepository<UserTimetable, Integer> {
    List<UserTimetable> findByUserAndDeletedAtIsNull(Users user);

    Optional<UserTimetable> findByIdAndDeletedAtIsNull(Integer timetableId);

    @Query("SELECT ut FROM UserTimetable ut JOIN FETCH ut.user WHERE ut.id = :timetableId AND ut.deletedAt IS NULL")
    Optional<UserTimetable> findByIdWithUserAndDeletedAtIsNull(@Param("timetableId") Integer timetableId);
}
