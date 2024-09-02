package com.sevenstars.roome.domain.review.service;

import com.sevenstars.roome.domain.review.entity.Theme;
import com.sevenstars.roome.domain.review.repository.ThemeRepository;
import com.sevenstars.roome.domain.review.response.StoresResponse;
import com.sevenstars.roome.domain.review.response.ThemesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public StoresResponse getStores(String storeName) {

        if (StringUtils.hasText(storeName)) {
            return new StoresResponse(themeRepository.findByStoreName(storeName.trim()));
        }

        return new StoresResponse(themeRepository.findAllStoreName());
    }

    public ThemesResponse getThemes(String name) {

        if (StringUtils.hasText(name)) {
            return new ThemesResponse(themeRepository.findByThemeName(name.trim()));
        }

        return new ThemesResponse(themeRepository.findAllStoreName());
    }

    @Transactional
    public void saveThemes(List<Theme> themes) {
        Map<String, Theme> savedThemes = getSavedThemes();

        markAllAsDeleted(savedThemes);

        for (Theme theme : themes) {
            String key = getKey(theme);
            Theme savedTheme = savedThemes.get(key);

            if (savedTheme == null) {
                themeRepository.save(theme);
            } else {
                savedTheme.update(theme);
            }
        }
    }

    private Map<String, Theme> getSavedThemes() {
        return themeRepository.findAll().stream()
                .collect(Collectors.toMap(
                        this::getKey,
                        theme -> theme
                ));
    }

    private void markAllAsDeleted(Map<String, Theme> themes) {
        themes.values().forEach(Theme::markAsDeleted);
    }

    private String getKey(Theme theme) {
        return theme.getArea() + "|" + theme.getStoreName() + "|" + theme.getName();
    }
}
