package com.sevenstars.roome.domain.profile.repository.genre;

import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.genre.PreferredGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferredGenreRepository extends JpaRepository<PreferredGenre, Long> {
    List<PreferredGenre> findByProfile(Profile profile);
}
