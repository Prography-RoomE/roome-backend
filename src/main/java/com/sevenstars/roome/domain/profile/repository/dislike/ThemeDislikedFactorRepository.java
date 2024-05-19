package com.sevenstars.roome.domain.profile.repository.dislike;

import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.dislike.ThemeDislikedFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemeDislikedFactorRepository extends JpaRepository<ThemeDislikedFactor, Long> {
    List<ThemeDislikedFactor> findByProfile(Profile profile);
}
