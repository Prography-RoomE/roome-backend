package com.sevenstars.roome.domain.review.repository;

import com.sevenstars.roome.domain.review.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ThemeRepository extends JpaRepository<Theme, Long> {

    @Query("SELECT DISTINCT t.storeName FROM Theme t ORDER BY t.storeName")
    List<String> findAllStoreName();

    @Query("SELECT DISTINCT t.storeName FROM Theme t WHERE t.storeName LIKE %:storeName% ORDER BY t.storeName")
    List<String> findByStoreName(String storeName);

    @Query("SELECT DISTINCT t.name FROM Theme t ORDER BY t.name")
    List<String> findAllThemeName();

    @Query("SELECT DISTINCT t.name FROM Theme t WHERE t.name LIKE %:name% ORDER BY t.name")
    List<String> findByThemeName(String name);
}
