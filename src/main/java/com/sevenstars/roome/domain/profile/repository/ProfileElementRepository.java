package com.sevenstars.roome.domain.profile.repository;

import com.sevenstars.roome.domain.profile.entity.ElementType;
import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.ProfileElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfileElementRepository extends JpaRepository<ProfileElement, Long> {

    List<ProfileElement> findByProfile(Profile profile);

    @Query("SELECT pe FROM ProfileElement pe " +
            "JOIN FETCH pe.element e " +
            "WHERE pe.profile.id = :profileId " +
            "AND e.isDeleted = false")
    List<ProfileElement> findByProfileId(Long profileId);

    @Query("SELECT pe FROM ProfileElement pe " +
            "LEFT JOIN FETCH pe.element e " +
            "WHERE pe.profile.id = :profileId " +
            "AND pe.type = :type " +
            "AND (e IS NULL OR e.isDeleted = false)")
    List<ProfileElement> findByProfileIdAndType(Long profileId, ElementType type);
}
