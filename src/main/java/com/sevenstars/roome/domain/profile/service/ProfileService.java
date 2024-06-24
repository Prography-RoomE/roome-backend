package com.sevenstars.roome.domain.profile.service;

import com.sevenstars.roome.domain.profile.entity.*;
import com.sevenstars.roome.domain.profile.entity.color.Color;
import com.sevenstars.roome.domain.profile.entity.room.RoomCountRange;
import com.sevenstars.roome.domain.profile.repository.ElementRepository;
import com.sevenstars.roome.domain.profile.repository.ProfileElementRepository;
import com.sevenstars.roome.domain.profile.repository.ProfileRepository;
import com.sevenstars.roome.domain.profile.repository.color.ColorRepository;
import com.sevenstars.roome.domain.profile.repository.room.RoomCountRangeRepository;
import com.sevenstars.roome.domain.profile.request.*;
import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.sevenstars.roome.domain.profile.entity.ElementType.*;
import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final RoomCountRangeRepository roomCountRangeRepository;
    private final ElementRepository elementRepository;
    private final ColorRepository colorRepository;
    private final ProfileElementRepository profileElementRepository;

    @Transactional(readOnly = true)
    public ProfileDefaultResponse getProfileDefaults() {

        List<RoomCountRange> roomCountRanges = roomCountRangeRepository.findAll();

        Map<ElementType, List<Element>> sortedMap = elementRepository.findByIsDeletedFalse().stream()
                .collect(Collectors.groupingBy(
                        Element::getType,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Element::getPriority))
                                        .collect(Collectors.toList())
                        )
                ));

        List<Element> genres = sortedMap.get(PREFERRED_GENRE);
        List<Element> strengths = sortedMap.get(USER_STRENGTH);
        List<Element> importantFactors = sortedMap.get(ElementType.THEME_IMPORTANT_FACTOR);
        List<Element> horrorThemePositions = sortedMap.get(ElementType.HORROR_THEME_POSITION);
        List<Element> hintUsagePreferences = sortedMap.get(ElementType.HINT_USAGE_PREFERENCE);
        List<Element> deviceLockPreferences = sortedMap.get(ElementType.DEVICE_LOCK_PREFERENCE);
        List<Element> activities = sortedMap.get(ElementType.ACTIVITY);
        List<Element> dislikedFactors = sortedMap.get(ElementType.THEME_DISLIKED_FACTOR);

        List<Color> colors = colorRepository.findByIsDeletedIsFalseOrderByPriorityAsc();

        return ProfileDefaultResponse.of(
                roomCountRanges,
                genres,
                strengths,
                importantFactors,
                horrorThemePositions,
                hintUsagePreferences,
                deviceLockPreferences,
                activities,
                dislikedFactors,
                colors);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Map<ElementType, List<Element>> sortedMap = profileElementRepository.findByProfileId(profile.getId()).stream()
                .map(ProfileElement::getElement)
                .collect(Collectors.groupingBy(
                        Element::getType,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Element::getPriority))
                                        .collect(Collectors.toList())
                        )
                ));

        // 미리 필요한 키들을 초기화
        Arrays.stream(ElementType.values())
                .forEach(key -> sortedMap.putIfAbsent(key, new ArrayList<>()));

        List<Element> genres = sortedMap.get(PREFERRED_GENRE);
        List<Element> strengths = sortedMap.get(USER_STRENGTH);
        List<Element> importantFactors = sortedMap.get(THEME_IMPORTANT_FACTOR);
        List<Element> horrorThemePositions = sortedMap.get(HORROR_THEME_POSITION);
        List<Element> hintUsagePreferences = sortedMap.get(HINT_USAGE_PREFERENCE);
        List<Element> deviceLockPreferences = sortedMap.get(DEVICE_LOCK_PREFERENCE);
        List<Element> activities = sortedMap.get(ACTIVITY);
        List<Element> dislikedFactors = sortedMap.get(THEME_DISLIKED_FACTOR);

        return ProfileResponse.of(profile,
                genres,
                strengths,
                importantFactors,
                horrorThemePositions,
                hintUsagePreferences,
                deviceLockPreferences,
                activities,
                dislikedFactors);
    }

    @Transactional
    public void clearProfile(Long userId) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        profile.clear();

        List<ProfileElement> profileElements = profileElementRepository.findByProfileId(profile.getId());
        profileElements.forEach(ProfileElement::clear);
    }

    @Transactional
    public void updateRoomCount(Long userId, RoomCountRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Integer count = request.getCount();

        profile.updateRoomCount(count);
    }

    @Transactional
    public void updateRoomCountRange(Long userId, RoomCountRangeRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Integer minCount = request.getMinCount();
        Integer maxCount = request.getMaxCount();

        profile.updateRoomCountRange(minCount, maxCount);
    }

    @Transactional
    public void updateMbti(Long userId, MbtiRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Mbti mbti = Arrays.stream(Mbti.values())
                .filter(value -> value.name().equals(request.getMbti()))
                .findAny()
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_MBTI_NOT_FOUND));

        profile.updateMbti(mbti);
    }

    @Transactional
    public void updateColor(Long userId, ColorRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Long id = request.getId();

        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_ELEMENT_ID_NOT_FOUND));

        profile.updateColor(color);
    }

    @Transactional
    public void updateProfileElement(Long userId, ProfileElementRequest request, ElementType type) {

        List<Long> ids = List.of(request.getId());
        updateProfileElements(userId, ids, type);
    }

    @Transactional
    public void updateProfileElements(Long userId, ProfileElementsRequest request, ElementType type) {

        List<Long> ids = request.getIds();
        updateProfileElements(userId, ids, type);
    }

    private void updateProfileElements(Long userId, List<Long> ids, ElementType type) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        List<Element> elements = elementRepository.findByIdInAndTypeAndIsDeletedFalse(ids, type);

        if (ids.size() > type.getMaxSize()) {
            throw new CustomClientErrorException(PROFILE_ELEMENT_ID_EXCEEDED);
        }

        if (ids.size() != elements.size()) {
            throw new CustomClientErrorException(PROFILE_ELEMENT_ID_NOT_FOUND);
        }

        List<ProfileElement> profileElements = profileElementRepository.findByProfileIdAndType(profile.getId(), type);
        int index = 0;

        profileElements.forEach(ProfileElement::clear);

        for (; index < elements.size() && index < profileElements.size(); index++) {
            profileElements.get(index).update(elements.get(index));
        }

        for (; index < elements.size(); index++) {
            profileElementRepository.save(new ProfileElement(profile, elements.get(index), type));
        }

        ProfileState state = type.getProfileState();
        Consumer<Profile> consumer = state.getProfileStateUpdateConsumer();
        consumer.accept(profile);
    }
}
