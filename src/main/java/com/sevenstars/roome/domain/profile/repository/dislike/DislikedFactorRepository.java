package com.sevenstars.roome.domain.profile.repository.dislike;

import com.sevenstars.roome.domain.profile.entity.dislike.DislikedFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DislikedFactorRepository extends JpaRepository<DislikedFactor, Long> {
    List<DislikedFactor> findByIsDeletedIsFalseOrderByPriorityAsc();
}
