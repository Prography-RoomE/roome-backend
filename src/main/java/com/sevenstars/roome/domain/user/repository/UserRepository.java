package com.sevenstars.roome.domain.user.repository;

import com.sevenstars.roome.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndWithdrawalFalse(Long id);

    Optional<User> findByServiceIdAndServiceUserIdAndWithdrawalFalse(String serviceId, String serviceUserId);
}
