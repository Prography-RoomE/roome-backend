package com.sevenstars.roome.domain.profile.repository.strength;

import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.strength.UserStrength;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStrengthRepository extends JpaRepository<UserStrength, Long> {
    List<UserStrength> findByProfile(Profile profile);

    void deleteByProfile(Profile profile);
}
