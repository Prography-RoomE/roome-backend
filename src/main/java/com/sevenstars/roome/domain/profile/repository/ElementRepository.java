package com.sevenstars.roome.domain.profile.repository;

import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.profile.entity.ElementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementRepository extends JpaRepository<Element, Long> {

    List<Element> findByIsDeletedFalse();

    List<Element> findByIdInAndTypeAndIsDeletedFalse(List<Long> ids, ElementType type);
}
