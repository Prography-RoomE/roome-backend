package com.sevenstars.roome.domain.profile.repository.important;

import com.sevenstars.roome.domain.profile.entity.important.ImportantFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportantFactorRepository extends JpaRepository<ImportantFactor, Long> {
    List<ImportantFactor> findByIsDeletedIsFalseOrderByPriorityAsc();
}
