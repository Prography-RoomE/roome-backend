package com.sevenstars.roome.domain.profile.repository;

import com.sevenstars.roome.domain.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
