package com.sevenstars.roome.domain.profile.service;

import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.ProfileElement;
import com.sevenstars.roome.domain.profile.entity.ProfileState;
import com.sevenstars.roome.domain.profile.repository.ElementRepository;
import com.sevenstars.roome.domain.profile.repository.ProfileElementRepository;
import com.sevenstars.roome.domain.profile.repository.ProfileRepository;
import com.sevenstars.roome.domain.profile.request.ProfileElementsRequest;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.sevenstars.roome.domain.profile.entity.ElementType.PREFERRED_GENRE;

@Transactional
@SpringBootTest
class ProfileServiceTest {

    @Autowired
    ProfileService profileService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ProfileElementRepository profileElementRepository;
    @Autowired
    ElementRepository elementRepository;


    @DisplayName("프로필 선호 장르 업데이트, 0개 -> 2개")
    @Test
    void updateProfileElementsTest1() {

        // Given
        User user = new User("google", "googleId", "roome@gmail.com");
        Profile profile = new Profile(user);

        List<Element> genres = List.of(new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4));

        userRepository.save(user);
        profileRepository.save(profile);
        elementRepository.saveAll(genres);

        Long userId = user.getId();
        Long profileId = profile.getId();
        List<Long> ids = List.of(genres.get(0).getId(), genres.get(1).getId());
        ProfileElementsRequest request = new ProfileElementsRequest();
        request.setIds(ids);

        // When
        profileService.updateProfileElements(userId, request, PREFERRED_GENRE);

        // Then
        List<ProfileElement> profileElements = profileElementRepository.findByProfileIdAndType(profileId, PREFERRED_GENRE);
        Assertions.assertThat(profileElements.get(0).getElement().getId()).isEqualTo(genres.get(0).getId());
        Assertions.assertThat(profileElements.get(1).getElement().getId()).isEqualTo(genres.get(1).getId());
    }

    @DisplayName("프로필 선호 장르 업데이트, 1개 -> 2개")
    @Test
    void updateProfileElementsTest2() {

        // Given
        User user = new User("google", "googleId", "roome@gmail.com");
        Profile profile = new Profile(user);
        List<Element> genres = List.of(new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4));

        userRepository.save(user);
        profileRepository.save(profile);
        elementRepository.saveAll(genres);
        profileElementRepository.save(new ProfileElement(profile, genres.get(0), PREFERRED_GENRE));

        Long userId = user.getId();
        Long profileId = profile.getId();
        List<Long> ids = List.of(genres.get(1).getId(), genres.get(2).getId());
        ProfileElementsRequest request = new ProfileElementsRequest();
        request.setIds(ids);

        // When
        profileService.updateProfileElements(userId, request, PREFERRED_GENRE);

        // Then
        List<ProfileElement> profileElements = profileElementRepository.findByProfileIdAndType(profileId, PREFERRED_GENRE);
        Assertions.assertThat(profileElements.size()).isEqualTo(2);
        Assertions.assertThat(profileElements.get(0).getElement().getId()).isEqualTo(genres.get(1).getId());
        Assertions.assertThat(profileElements.get(1).getElement().getId()).isEqualTo(genres.get(2).getId());
    }

    @DisplayName("프로필 선호 장르 업데이트, 2개 -> 1개")
    @Test
    void updateProfileElementsTest3() {

        // Given
        User user = new User("google", "googleId", "roome@gmail.com");
        Profile profile = new Profile(user);
        List<Element> genres = List.of(new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4));

        userRepository.save(user);
        profileRepository.save(profile);
        elementRepository.saveAll(genres);
        profileElementRepository.save(new ProfileElement(profile, genres.get(0), PREFERRED_GENRE));
        profileElementRepository.save(new ProfileElement(profile, genres.get(1), PREFERRED_GENRE));

        Long userId = user.getId();
        Long profileId = profile.getId();
        List<Long> ids = List.of(genres.get(2).getId());
        ProfileElementsRequest request = new ProfileElementsRequest();
        request.setIds(ids);

        // When
        profileService.updateProfileElements(userId, request, PREFERRED_GENRE);

        // Then
        List<ProfileElement> profileElements = profileElementRepository.findByProfileIdAndType(profileId, PREFERRED_GENRE);
        Assertions.assertThat(profileElements.size()).isEqualTo(2);
        Assertions.assertThat(profileElements.get(0).getElement().getId()).isEqualTo(genres.get(2).getId());
        Assertions.assertThat(profileElements.get(1).getElement()).isNull();
    }

    @DisplayName("프로필 선호 장르 업데이트, 2개 -> 2개")
    @Test
    void updateProfileElementsTest4() {

        // Given
        User user = new User("google", "googleId", "roome@gmail.com");
        Profile profile = new Profile(user);
        List<Element> genres = List.of(new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4));

        userRepository.save(user);
        profileRepository.save(profile);
        elementRepository.saveAll(genres);
        profileElementRepository.save(new ProfileElement(profile, genres.get(0), PREFERRED_GENRE));
        profileElementRepository.save(new ProfileElement(profile, genres.get(1), PREFERRED_GENRE));

        Long userId = user.getId();
        Long profileId = profile.getId();
        List<Long> ids = List.of(genres.get(2).getId(), genres.get(3).getId());
        ProfileElementsRequest request = new ProfileElementsRequest();
        request.setIds(ids);

        // When
        profileService.updateProfileElements(userId, request, PREFERRED_GENRE);

        // Then
        List<ProfileElement> profileElements = profileElementRepository.findByProfileIdAndType(profileId, PREFERRED_GENRE);
        Assertions.assertThat(profileElements.size()).isEqualTo(2);
        Assertions.assertThat(profileElements.get(0).getElement().getId()).isEqualTo(genres.get(2).getId());
        Assertions.assertThat(profileElements.get(1).getElement().getId()).isEqualTo(genres.get(3).getId());
    }

    @DisplayName("프로필 선호 장르 업데이트, 2개 -> 2개")
    @Test
    void updateProfileElementsTest5() {

        // Given
        User user = new User("google", "googleId", "roome@gmail.com");
        Profile profile = new Profile(user);
        List<Element> genres = List.of(new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4));

        userRepository.save(user);
        profileRepository.save(profile);
        elementRepository.saveAll(genres);
        profileElementRepository.save(new ProfileElement(profile, genres.get(0), PREFERRED_GENRE));
        profileElementRepository.save(new ProfileElement(profile, genres.get(1), PREFERRED_GENRE));

        Long userId = user.getId();
        Long profileId = profile.getId();
        List<Long> ids = List.of(genres.get(0).getId(), genres.get(4).getId());
        ProfileElementsRequest request = new ProfileElementsRequest();
        request.setIds(ids);

        // When
        profileService.updateProfileElements(userId, request, PREFERRED_GENRE);

        // Then
        List<ProfileElement> profileElements = profileElementRepository.findByProfileIdAndType(profileId, PREFERRED_GENRE);
        Assertions.assertThat(profileElements.size()).isEqualTo(2);
        Assertions.assertThat(profileElements.get(0).getElement().getId()).isEqualTo(genres.get(0).getId());
        Assertions.assertThat(profileElements.get(1).getElement().getId()).isEqualTo(genres.get(4).getId());
    }
}
