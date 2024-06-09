package com.sevenstars.roome.domain.user.repository;

import com.sevenstars.roome.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndWithdrawalIsFalse(Long id);

    Optional<User> findByServiceIdAndServiceUserIdAndWithdrawalIsFalse(String serviceId, String serviceUserId);

    boolean existsByNicknameAndWithdrawalIsFalse(String nickname);
}
