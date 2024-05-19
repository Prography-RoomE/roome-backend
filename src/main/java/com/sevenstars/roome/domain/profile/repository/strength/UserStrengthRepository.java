package com.sevenstars.roome.domain.profile.repository.strength;

import com.sevenstars.roome.domain.profile.entity.strength.UserStrength;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStrengthRepository extends JpaRepository<UserStrength, Long> {
}
