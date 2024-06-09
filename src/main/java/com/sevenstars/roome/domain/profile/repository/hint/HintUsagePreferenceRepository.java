package com.sevenstars.roome.domain.profile.repository.hint;

import com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HintUsagePreferenceRepository extends JpaRepository<HintUsagePreference, Long> {
    List<HintUsagePreference> findByIsDeletedIsFalseOrderByPriorityAsc();
}
