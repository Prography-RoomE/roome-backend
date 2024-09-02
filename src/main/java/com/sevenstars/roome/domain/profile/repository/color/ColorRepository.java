package com.sevenstars.roome.domain.profile.repository.color;

import com.sevenstars.roome.domain.profile.entity.color.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColorRepository extends JpaRepository<Color, Long> {
    List<Color> findByIsDeletedIsFalseOrderByPriorityAsc();
}
