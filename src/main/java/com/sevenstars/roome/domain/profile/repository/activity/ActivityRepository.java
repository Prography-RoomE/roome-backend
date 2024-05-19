package com.sevenstars.roome.domain.profile.repository.activity;

import com.sevenstars.roome.domain.profile.entity.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByIsDeletedIsFalseOrderByPriorityAsc();
}
