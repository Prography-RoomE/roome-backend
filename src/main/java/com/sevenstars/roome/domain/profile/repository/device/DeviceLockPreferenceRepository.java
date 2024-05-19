package com.sevenstars.roome.domain.profile.repository.device;

import com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceLockPreferenceRepository extends JpaRepository<DeviceLockPreference, Long> {
    List<DeviceLockPreference> findByIsDeletedIsFalseOrderByPriorityAsc();
}
