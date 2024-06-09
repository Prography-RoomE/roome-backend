package com.sevenstars.roome.domain.profile.repository.strength;

import com.sevenstars.roome.domain.profile.entity.strength.Strength;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StrengthRepository extends JpaRepository<Strength, Long> {
    List<Strength> findByIsDeletedIsFalseOrderByPriorityAsc();
}
