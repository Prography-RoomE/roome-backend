package com.sevenstars.roome.domain.profile.repository;

import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("SELECT p FROM Profile p " +
            "LEFT JOIN FETCH p.horrorThemePosition " +
            "LEFT JOIN FETCH p.hintUsagePreference " +
            "LEFT JOIN FETCH p.deviceLockPreference " +
            "LEFT JOIN FETCH p.activity " +
            "LEFT JOIN FETCH p.color " +
            "WHERE p.user = :user")
    Optional<Profile> findByUser(User user);
}
