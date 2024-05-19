package com.sevenstars.roome.domain.profile.repository.position;

import com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorrorThemePositionRepository extends JpaRepository<HorrorThemePosition, Long> {
    List<HorrorThemePosition> findByIsDeletedIsFalseOrderByPriorityAsc();
}
