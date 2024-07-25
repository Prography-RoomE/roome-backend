package com.sevenstars.roome.domain.user.repository;

import com.sevenstars.roome.domain.user.entity.UserDeactivationReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeactivationReasonRepository extends JpaRepository<UserDeactivationReason, Long> {
}
