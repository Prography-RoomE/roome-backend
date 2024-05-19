package com.sevenstars.roome.domain.profile.repository.genre;

import com.sevenstars.roome.domain.profile.entity.genre.PreferredGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredGenreRepository extends JpaRepository<PreferredGenre, Long> {
}
