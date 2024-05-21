package com.sevenstars.roome.domain.profile.repository.important;

import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.important.ThemeImportantFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemeImportantFactorRepository extends JpaRepository<ThemeImportantFactor, Long> {
    List<ThemeImportantFactor> findByProfile(Profile profile);

    void deleteByProfile(Profile profile);
}
