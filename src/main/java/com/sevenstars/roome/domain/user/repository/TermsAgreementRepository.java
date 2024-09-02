package com.sevenstars.roome.domain.user.repository;

import com.sevenstars.roome.domain.user.entity.TermsAgreement;
import com.sevenstars.roome.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermsAgreementRepository extends JpaRepository<TermsAgreement, Long> {
    Optional<TermsAgreement> findByUser(User user);
}
