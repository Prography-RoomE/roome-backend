package com.sevenstars.roome.domain.profile.repository;

import com.sevenstars.roome.domain.profile.entity.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.sevenstars.roome.domain.profile.entity.ElementType.PREFERRED_GENRE;
import static com.sevenstars.roome.domain.profile.entity.ElementType.USER_STRENGTH;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ElementRepositoryTest {

    @Autowired
    ElementRepository elementRepository;

    @DisplayName("요소를 타입으로 조회시 우선순위로 오름차순 정렬하여 출력한다.")
    @Test
    void findByTypeTest1() {

        // Given
        List<Element> elements = List.of(
                new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4),
                new Element(PREFERRED_GENRE, "스릴러", "", "", "", "", 5),
                new Element(USER_STRENGTH, "관찰력", "", "", "", "", 0),
                new Element(USER_STRENGTH, "분석력", "", "", "", "", 1),
                new Element(USER_STRENGTH, "추리력", "", "", "", "", 2));

        elementRepository.saveAll(elements);

        // When
        List<Element> genres = elementRepository.findByTypeAndIsDeletedFalseOrderByPriorityAsc(PREFERRED_GENRE);

        // Then
        assertThat(genres.get(0).getTitle()).isEqualTo(elements.get(0).getTitle());
        assertThat(genres.get(1).getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(genres.get(2).getTitle()).isEqualTo(elements.get(2).getTitle());
        assertThat(genres.get(3).getTitle()).isEqualTo(elements.get(3).getTitle());
        assertThat(genres.get(4).getTitle()).isEqualTo(elements.get(4).getTitle());
        assertThat(genres.get(5).getTitle()).isEqualTo(elements.get(5).getTitle());
    }

    @DisplayName("요소를 타입으로 조회시 우선순위로 오름차순 정렬하여 출력한다.")
    @Test
    void findByTypeTest2() {

        // Given
        List<Element> elements = List.of(
                new Element(PREFERRED_GENRE, "액션", "", "", "", "", 5),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 4),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "스릴러", "", "", "", "", 0),
                new Element(USER_STRENGTH, "관찰력", "", "", "", "", 0),
                new Element(USER_STRENGTH, "분석력", "", "", "", "", 1),
                new Element(USER_STRENGTH, "추리력", "", "", "", "", 2));

        elementRepository.saveAll(elements);

        // When
        List<Element> genres = elementRepository.findByTypeAndIsDeletedFalseOrderByPriorityAsc(PREFERRED_GENRE);

        // Then
        assertThat(genres.get(0).getTitle()).isEqualTo(elements.get(5).getTitle());
        assertThat(genres.get(1).getTitle()).isEqualTo(elements.get(4).getTitle());
        assertThat(genres.get(2).getTitle()).isEqualTo(elements.get(3).getTitle());
        assertThat(genres.get(3).getTitle()).isEqualTo(elements.get(2).getTitle());
        assertThat(genres.get(4).getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(genres.get(5).getTitle()).isEqualTo(elements.get(0).getTitle());
    }

    @DisplayName("요소를 타입과 ID로 조회시 우선순위로 오름차순 정렬하여 출력한다.")
    @Test
    void findByIdInAndTypeTest() {

        // Given
        List<Element> elements = List.of(
                new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4),
                new Element(PREFERRED_GENRE, "스릴러", "", "", "", "", 5),
                new Element(USER_STRENGTH, "관찰력", "", "", "", "", 0),
                new Element(USER_STRENGTH, "분석력", "", "", "", "", 1),
                new Element(USER_STRENGTH, "추리력", "", "", "", "", 2));

        elementRepository.saveAll(elements);

        // When
        List<Long> ids = List.of(elements.get(5).getId(), elements.get(0).getId());
        List<Element> genres = elementRepository.findByIdInAndTypeAndIsDeletedFalseOrderByPriorityAsc(ids, PREFERRED_GENRE);

        // Then
        assertThat(genres.get(0).getTitle()).isEqualTo(elements.get(0).getTitle());
        assertThat(genres.get(1).getTitle()).isEqualTo(elements.get(5).getTitle());
    }
}
