package com.sevenstars.roome.domain.common.repository;

import com.sevenstars.roome.domain.common.entity.ForbiddenWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Long> {
    @Query("SELECT COUNT(f) > 0 FROM ForbiddenWord f WHERE :nickname LIKE CONCAT('%', f.word, '%')")
    boolean hasForbiddenWordsInNickname(@Param("nickname") String nickname);
}
