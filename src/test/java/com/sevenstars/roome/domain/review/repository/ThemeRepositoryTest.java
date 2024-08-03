package com.sevenstars.roome.domain.review.repository;

import com.sevenstars.roome.domain.review.entity.Theme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ThemeRepositoryTest {

    @Autowired
    ThemeRepository themeRepository;

    @DisplayName("매장 이름, 테마 이름으로 조회시 중복을 제거하고 오름차순 정렬하여 출력한다.")
    @Test
    void findAllTest() {

        // Given
        List<Theme> themes = List.of(new Theme("홍대", "제로월드 홍대점", "층간소음", 9.49, "", 0),
                new Theme("홍대", "티켓 투 이스케이프", "갤럭시 익스프레스", 9.41, "", 0),
                new Theme("홍대", "오아시스", "배드 타임 (BÆD TIME)", 9.39, "", 0),
                new Theme("홍대", "제로월드 홍대점", "ALIVE", 9.30, "", 0),
                new Theme("홍대", "지구별 방탈출 홍대라스트시티점", "섀도우", 9.28, "", 0));

        themeRepository.saveAll(themes);

        // When
        List<String> allStoreNames = themeRepository.findAllStoreName();
        List<String> storeNames = themeRepository.findByStoreName("월드");
        List<String> allThemeNames = themeRepository.findAllThemeName();
        List<String> themeNames = themeRepository.findByThemeName("I");

        // Then
        assertThat(allStoreNames.size()).isEqualTo(4);
        assertThat(allStoreNames.get(0)).isEqualTo(themes.get(2).getStoreName());
        assertThat(allStoreNames.get(1)).isEqualTo(themes.get(0).getStoreName());
        assertThat(allStoreNames.get(2)).isEqualTo(themes.get(4).getStoreName());
        assertThat(allStoreNames.get(3)).isEqualTo(themes.get(1).getStoreName());

        assertThat(allThemeNames.size()).isEqualTo(5);
        assertThat(allThemeNames.get(0)).isEqualTo(themes.get(3).getName());
        assertThat(allThemeNames.get(1)).isEqualTo(themes.get(1).getName());
        assertThat(allThemeNames.get(2)).isEqualTo(themes.get(2).getName());
        assertThat(allThemeNames.get(3)).isEqualTo(themes.get(4).getName());
        assertThat(allThemeNames.get(4)).isEqualTo(themes.get(0).getName());

        assertThat(storeNames.size()).isEqualTo(1);
        assertThat(storeNames.get(0)).isEqualTo(themes.get(0).getStoreName());

        assertThat(themeNames.size()).isEqualTo(2);
        assertThat(themeNames.get(0)).isEqualTo(themes.get(3).getName());
        assertThat(themeNames.get(1)).isEqualTo(themes.get(2).getName());
    }
}
