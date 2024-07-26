package com.sevenstars.roome.global.version.repository;

import com.sevenstars.roome.global.version.entity.Version;
import com.sevenstars.roome.global.version.entity.VersionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VersionRepository extends JpaRepository<Version, Long> {

    Optional<Version> findByType(VersionType type);
}
