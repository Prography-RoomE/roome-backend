package com.sevenstars.roome.docs.user;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.domain.profile.controller.ProfileController;
import com.sevenstars.roome.domain.profile.entity.Mbti;
import com.sevenstars.roome.domain.profile.entity.color.ColorDirection;
import com.sevenstars.roome.domain.profile.entity.color.ColorShape;
import com.sevenstars.roome.domain.profile.request.*;
import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.domain.profile.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.sevenstars.roome.domain.profile.entity.ProfileState.COMPLETE;
import static com.sevenstars.roome.domain.profile.entity.color.ColorDirection.TOP_LEFT_TO_BOTTOM_RIGHT;
import static com.sevenstars.roome.domain.profile.entity.color.ColorMode.GRADIENT;
import static com.sevenstars.roome.domain.profile.entity.color.ColorMode.SOLID;
import static com.sevenstars.roome.domain.profile.entity.color.ColorShape.LINEAR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerDocsTest extends RestDocsTest {

    private final ProfileService profileService = mock(ProfileService.class);

    @Override
    protected Object initializeController() {
        return new ProfileController(profileService);
    }

    @DisplayName("프로필 기본 정보 조회")
    @Test
    void getProfileDefaults() throws Exception {

        // Given
        List<ProfileDefaultResponse.RoomCountRange> roomCountRanges = List.of(
                new ProfileDefaultResponse.RoomCountRange(1L, "0~30번", 0, 30),
                new ProfileDefaultResponse.RoomCountRange(2L, "31~60번", 31, 60),
                new ProfileDefaultResponse.RoomCountRange(3L, "61~99번", 61, 99),
                new ProfileDefaultResponse.RoomCountRange(4L, "100~150번", 100, 150),
                new ProfileDefaultResponse.RoomCountRange(5L, "151~200번", 151, 200),
                new ProfileDefaultResponse.RoomCountRange(6L, "201~300번", 201, 300),
                new ProfileDefaultResponse.RoomCountRange(7L, "301번 이상", 301, 99999));

        List<ProfileDefaultResponse.Genre> genres = List.of(
                new ProfileDefaultResponse.Genre(1L, "공포"),
                new ProfileDefaultResponse.Genre(2L, "스릴러"));

        List<ProfileDefaultResponse.Strength> strengths = List.of(
                new ProfileDefaultResponse.Strength(1L, "관찰력"),
                new ProfileDefaultResponse.Strength(2L, "분석력"));

        List<ProfileDefaultResponse.ImportantFactor> importantFactors = List.of(
                new ProfileDefaultResponse.ImportantFactor(1L, "탄탄한 스토리"),
                new ProfileDefaultResponse.ImportantFactor(2L, "다양한 연출"));

        List<ProfileDefaultResponse.HorrorThemePosition> horrorThemePositions = List.of(
                new ProfileDefaultResponse.HorrorThemePosition(1L, "극쫄", "사소한 거에도 놀랄 정도로 겁이 많아요"),
                new ProfileDefaultResponse.HorrorThemePosition(2L, "쫄", "겁이 많은 편이에요"));

        List<ProfileDefaultResponse.HintUsagePreference> hintUsagePreferences = List.of(
                new ProfileDefaultResponse.HintUsagePreference(1L, "한트 안써요", "막히더라도 사용하지 않아요"),
                new ProfileDefaultResponse.HintUsagePreference(2L, "최소한의 힌트만", "시간이 오래 걸릴 때만 써요"),
                new ProfileDefaultResponse.HintUsagePreference(3L, "힌트 사용 괜찮아요", "조금만 막혀도 바로 사용해요"));

        List<ProfileDefaultResponse.DeviceLockPreference> deviceLockPreferences = List.of(
                new ProfileDefaultResponse.DeviceLockPreference(1L, "장치", "특정 물체를 조작, 작동하여 진행되는 방식(센서, 기계 등)"),
                new ProfileDefaultResponse.DeviceLockPreference(2L, "자물쇠", "문제를 풀어 얻는 답 (숫자, 문자, 방향 등)이나 열쇠로 자물쇠를 푸는 방식"),
                new ProfileDefaultResponse.DeviceLockPreference(3L, "장치&자물쇠 모두", ""));

        List<ProfileDefaultResponse.Activity> activities = List.of(
                new ProfileDefaultResponse.Activity(1L, "높은 활동성", "걷고, 뛰고, 계단 이동하는 게 좋아요"),
                new ProfileDefaultResponse.Activity(2L, "중간 활동성", "땀나지 않을 정도로 적당히 움직이는 게 좋아요"));

        List<ProfileDefaultResponse.DislikedFactor> dislikedFactors = List.of(
                new ProfileDefaultResponse.DislikedFactor(1L, "이과형 문제"),
                new ProfileDefaultResponse.DislikedFactor(2L, "노가다 문제"));

        List<ProfileDefaultResponse.Color> colors = List.of(
                new ProfileDefaultResponse.Color(1L, "Gradient Red", GRADIENT, LINEAR, TOP_LEFT_TO_BOTTOM_RIGHT, "#FF453C", "#FFACB3"),
                new ProfileDefaultResponse.Color(1L, "Solid Black", SOLID, ColorShape.NONE, ColorDirection.NONE, "#000000", "#000000"));

        ProfileDefaultResponse response = new ProfileDefaultResponse(roomCountRanges,
                genres,
                strengths,
                importantFactors,
                horrorThemePositions,
                hintUsagePreferences,
                deviceLockPreferences,
                activities,
                dislikedFactors,
                colors);

        given(profileService.getProfileDefaults())
                .willReturn(response);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/profiles/defaults")
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.roomCountRanges[]").type(JsonFieldType.ARRAY)
                                                .description("방 수 범위 목록"),
                                        fieldWithPath("data.roomCountRanges[].id").type(JsonFieldType.NUMBER)
                                                .description("방 수 범위 ID"),
                                        fieldWithPath("data.roomCountRanges[].title").type(JsonFieldType.STRING)
                                                .description("방 수 범위 제목"),
                                        fieldWithPath("data.roomCountRanges[].minCount").type(JsonFieldType.NUMBER)
                                                .description("방 수 범위 최소값"),
                                        fieldWithPath("data.roomCountRanges[].maxCount").type(JsonFieldType.NUMBER)
                                                .description("방 수 범위 최대값"),
                                        fieldWithPath("data.genres[]").type(JsonFieldType.ARRAY)
                                                .description("선호 장르 목록"),
                                        fieldWithPath("data.genres[].id").type(JsonFieldType.NUMBER)
                                                .description("선호 장르 ID"),
                                        fieldWithPath("data.genres[].title").type(JsonFieldType.STRING)
                                                .description("선호 장르 제목"),
                                        fieldWithPath("data.strengths[]").type(JsonFieldType.ARRAY)
                                                .description("사용자 강점 목록"),
                                        fieldWithPath("data.strengths[].id").type(JsonFieldType.NUMBER)
                                                .description("사용자 강점 ID"),
                                        fieldWithPath("data.strengths[].title").type(JsonFieldType.STRING)
                                                .description("사용자 강점 제목"),
                                        fieldWithPath("data.importantFactors[]").type(JsonFieldType.ARRAY)
                                                .description("중요 요소 목록"),
                                        fieldWithPath("data.importantFactors[].id").type(JsonFieldType.NUMBER)
                                                .description("중요 요소 ID"),
                                        fieldWithPath("data.importantFactors[].title").type(JsonFieldType.STRING)
                                                .description("중요 요소 제목"),
                                        fieldWithPath("data.horrorThemePositions[]").type(JsonFieldType.ARRAY)
                                                .description("공포 테마 포지션 목록"),
                                        fieldWithPath("data.horrorThemePositions[].id").type(JsonFieldType.NUMBER)
                                                .description("공포 테마 포지션 ID"),
                                        fieldWithPath("data.horrorThemePositions[].title").type(JsonFieldType.STRING)
                                                .description("공포 테마 포지션 제목"),
                                        fieldWithPath("data.horrorThemePositions[].description").type(JsonFieldType.STRING)
                                                .description("공포 테마 포지션 설명"),
                                        fieldWithPath("data.hintUsagePreferences[]").type(JsonFieldType.ARRAY)
                                                .description("힌트 사용 선호도 목록"),
                                        fieldWithPath("data.hintUsagePreferences[].id").type(JsonFieldType.NUMBER)
                                                .description("힌트 사용 선호도 ID"),
                                        fieldWithPath("data.hintUsagePreferences[].title").type(JsonFieldType.STRING)
                                                .description("힌트 사용 선호도 제목"),
                                        fieldWithPath("data.hintUsagePreferences[].description").type(JsonFieldType.STRING)
                                                .description("힌트 사용 선호도 설명"),
                                        fieldWithPath("data.deviceLockPreferences[]").type(JsonFieldType.ARRAY)
                                                .description("장치 자물쇠 선호도 목록"),
                                        fieldWithPath("data.deviceLockPreferences[].id").type(JsonFieldType.NUMBER)
                                                .description("장치 자물쇠 선호도 ID"),
                                        fieldWithPath("data.deviceLockPreferences[].title").type(JsonFieldType.STRING)
                                                .description("장치 자물쇠 선호도 제목"),
                                        fieldWithPath("data.deviceLockPreferences[].description").type(JsonFieldType.STRING)
                                                .description("장치 자물쇠 선호도 설명"),
                                        fieldWithPath("data.activities[]").type(JsonFieldType.ARRAY)
                                                .description("활동성 목록"),
                                        fieldWithPath("data.activities[].id").type(JsonFieldType.NUMBER)
                                                .description("활동성 ID"),
                                        fieldWithPath("data.activities[].title").type(JsonFieldType.STRING)
                                                .description("활동성 제목"),
                                        fieldWithPath("data.activities[].description").type(JsonFieldType.STRING)
                                                .description("활동성 설명"),
                                        fieldWithPath("data.dislikedFactors[]").type(JsonFieldType.ARRAY)
                                                .description("싫어하는 요소 목록"),
                                        fieldWithPath("data.dislikedFactors[].id").type(JsonFieldType.NUMBER)
                                                .description("싫어하는 요소 ID"),
                                        fieldWithPath("data.dislikedFactors[].title").type(JsonFieldType.STRING)
                                                .description("싫어하는 요소 제목"),
                                        fieldWithPath("data.colors[]").type(JsonFieldType.ARRAY)
                                                .description("색상 목록"),
                                        fieldWithPath("data.colors[].id").type(JsonFieldType.NUMBER)
                                                .description("색상 ID"),
                                        fieldWithPath("data.colors[].title").type(JsonFieldType.STRING)
                                                .description("색상 제목"),
                                        fieldWithPath("data.colors[].mode").type(JsonFieldType.STRING)
                                                .description("색상 모드"),
                                        fieldWithPath("data.colors[].shape").type(JsonFieldType.STRING)
                                                .description("색상 모양"),
                                        fieldWithPath("data.colors[].direction").type(JsonFieldType.STRING)
                                                .description("색상 방향"),
                                        fieldWithPath("data.colors[].startColor").type(JsonFieldType.STRING)
                                                .description("색상 시작 색상"),
                                        fieldWithPath("data.colors[].endColor").type(JsonFieldType.STRING)
                                                .description("색상 종료 색상"))));
    }

    @DisplayName("프로필 정보 조회")
    @Test
    void getProfile() throws Exception {

        // Given
        List<ProfileResponse.Genre> genres = List.of(
                new ProfileResponse.Genre(9L, "미스터리"),
                new ProfileResponse.Genre(15L, "SF"));

        List<ProfileResponse.Strength> strengths = List.of(
                new ProfileResponse.Strength(4L, "집중력"),
                new ProfileResponse.Strength(8L, "침착함"));

        List<ProfileResponse.ImportantFactor> importantFactors = List.of(
                new ProfileResponse.ImportantFactor(3L, "명확한 개연성"),
                new ProfileResponse.ImportantFactor(7L, "논리적인 문제"));

        ProfileResponse.HorrorThemePosition horrorThemePosition = new ProfileResponse.HorrorThemePosition(4L, "마지모탱");

        ProfileResponse.HintUsagePreference hintUsagePreference = new ProfileResponse.HintUsagePreference(2L, "최소한의 힌트만");

        ProfileResponse.DeviceLockPreference deviceLockPreference = new ProfileResponse.DeviceLockPreference(1L, "장치");

        ProfileResponse.Activity activity = new ProfileResponse.Activity(3L, "낮은 활동성");

        List<ProfileResponse.DislikedFactor> dislikedFactors = List.of(
                new ProfileResponse.DislikedFactor(2L, "노가다 문제"),
                new ProfileResponse.DislikedFactor(6L, "높은 난이도"));

        ProfileResponse.Color color = new ProfileResponse.Color(4L,
                "Gradient Purple",
                GRADIENT,
                LINEAR,
                TOP_LEFT_TO_BOTTOM_RIGHT,
                "#2E2E8D",
                "#73549D");

        ProfileResponse response = new ProfileResponse(
                1L,
                COMPLETE,
                "150",
                genres,
                Mbti.ISFJ,
                strengths,
                importantFactors,
                horrorThemePosition,
                hintUsagePreference,
                deviceLockPreference,
                activity,
                dislikedFactors,
                color);

        given(profileService.getProfile(any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/profiles")
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                                .description("프로필 ID"),
                                        fieldWithPath("data.state").type(JsonFieldType.STRING)
                                                .description("프로필 상태"),
                                        fieldWithPath("data.count").type(JsonFieldType.STRING)
                                                .description("방 수"),
                                        fieldWithPath("data.preferredGenres[]").type(JsonFieldType.ARRAY)
                                                .description("선호 장르 목록"),
                                        fieldWithPath("data.preferredGenres[].id").type(JsonFieldType.NUMBER)
                                                .description("선호 장르 ID"),
                                        fieldWithPath("data.preferredGenres[].title").type(JsonFieldType.STRING)
                                                .description("선호 장르 제목"),
                                        fieldWithPath("data.mbti").type(JsonFieldType.STRING)
                                                .description("MBTI"),
                                        fieldWithPath("data.userStrengths[]").type(JsonFieldType.ARRAY)
                                                .description("사용자 강점 목록"),
                                        fieldWithPath("data.userStrengths[].id").type(JsonFieldType.NUMBER)
                                                .description("사용자 강점 ID"),
                                        fieldWithPath("data.userStrengths[].title").type(JsonFieldType.STRING)
                                                .description("사용자 강점 제목"),
                                        fieldWithPath("data.themeImportantFactors[]").type(JsonFieldType.ARRAY)
                                                .description("테마 중요 요소 목록"),
                                        fieldWithPath("data.themeImportantFactors[].id").type(JsonFieldType.NUMBER)
                                                .description("테마 중요 요소 ID"),
                                        fieldWithPath("data.themeImportantFactors[].title").type(JsonFieldType.STRING)
                                                .description("테마 중요 요소 제목"),
                                        fieldWithPath("data.horrorThemePosition.id").type(JsonFieldType.NUMBER)
                                                .description("공포 테마 포지션 ID"),
                                        fieldWithPath("data.horrorThemePosition.title").type(JsonFieldType.STRING)
                                                .description("공포 테마 포지션 제목"),
                                        fieldWithPath("data.hintUsagePreference.id").type(JsonFieldType.NUMBER)
                                                .description("힌트 사용 선호도 ID"),
                                        fieldWithPath("data.hintUsagePreference.title").type(JsonFieldType.STRING)
                                                .description("힌트 사용 선호도 제목"),
                                        fieldWithPath("data.deviceLockPreference.id").type(JsonFieldType.NUMBER)
                                                .description("장치 자물쇠 선호도 ID"),
                                        fieldWithPath("data.deviceLockPreference.title").type(JsonFieldType.STRING)
                                                .description("장치 자물쇠 선호도 제목"),
                                        fieldWithPath("data.activity.id").type(JsonFieldType.NUMBER)
                                                .description("활동성 ID"),
                                        fieldWithPath("data.activity.title").type(JsonFieldType.STRING)
                                                .description("활동성 제목"),
                                        fieldWithPath("data.themeDislikedFactors[]").type(JsonFieldType.ARRAY)
                                                .description("싫어하는 요소 목록"),
                                        fieldWithPath("data.themeDislikedFactors[].id").type(JsonFieldType.NUMBER)
                                                .description("싫어하는 요소 ID"),
                                        fieldWithPath("data.themeDislikedFactors[].title").type(JsonFieldType.STRING)
                                                .description("싫어하는 요소 제목"),
                                        fieldWithPath("data.color.id").type(JsonFieldType.NUMBER)
                                                .description("색상 ID"),
                                        fieldWithPath("data.color.title").type(JsonFieldType.STRING)
                                                .description("색상 제목"),
                                        fieldWithPath("data.color.mode").type(JsonFieldType.STRING)
                                                .description("색상 모드"),
                                        fieldWithPath("data.color.shape").type(JsonFieldType.STRING)
                                                .description("색상 모양"),
                                        fieldWithPath("data.color.direction").type(JsonFieldType.STRING)
                                                .description("색상 방향"),
                                        fieldWithPath("data.color.startColor").type(JsonFieldType.STRING)
                                                .description("색상 시작 색상"),
                                        fieldWithPath("data.color.endColor").type(JsonFieldType.STRING)
                                                .description("색상 종료 색상"))));
    }

    @DisplayName("방 수 저장")
    @Test
    void updateRoomCount() throws Exception {

        // Given
        RoomCountRequest request = new RoomCountRequest();
        request.setCount(150);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/room-count")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("방 수 범위 저장")
    @Test
    void updateRoomCountRange() throws Exception {

        // Given
        RoomCountRangeRequest request = new RoomCountRangeRequest();
        request.setMinCount(0);
        request.setMaxCount(30);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/room-count-range")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("선호 장르 저장")
    @Test
    void updatePreferredGenres() throws Exception {

        // Given
        PreferredGenresRequest request = new PreferredGenresRequest();
        request.setIds(List.of(9L, 15L));

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/preferred-genres")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("MBTI 저장")
    @Test
    void updateMbti() throws Exception {

        // Given
        MbtiRequest request = new MbtiRequest();
        request.setMbti("ISTJ");

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/mbti")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("사용자 강점 저장")
    @Test
    void updateThemeImportantFactors() throws Exception {

        // Given
        UserStrengthsRequest request = new UserStrengthsRequest();
        request.setIds(List.of(4L, 8L));

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/user-strengths")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("테마 중요 요소 저장")
    @Test
    void updateUserStrengths() throws Exception {

        // Given
        ThemeImportantFactorsRequest request = new ThemeImportantFactorsRequest();
        request.setIds(List.of(3L, 7L));

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/theme-important-factors")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("공포 테마 포지션 저장")
    @Test
    void updateHorrorThemePosition() throws Exception {

        // Given
        HorrorThemePositionRequest request = new HorrorThemePositionRequest();
        request.setId(4L);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/horror-theme-position")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("힌트 사용 선호도 저장")
    @Test
    void updateHintUsagePreference() throws Exception {

        // Given
        HintUsagePreferenceRequest request = new HintUsagePreferenceRequest();
        request.setId(2L);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/hint-usage-preference")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("장치 자물쇠 선호도 저장")
    @Test
    void updateDeviceLockPreference() throws Exception {

        // Given
        HintUsagePreferenceRequest request = new HintUsagePreferenceRequest();
        request.setId(2L);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/device-lock-preference")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("활동성 저장")
    @Test
    void updateActivity() throws Exception {

        // Given
        HintUsagePreferenceRequest request = new HintUsagePreferenceRequest();
        request.setId(3L);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/activity")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("테마 싫어하는 요소 저장")
    @Test
    void updateThemeDislikedFactors() throws Exception {

        // Given
        ThemeImportantFactorsRequest request = new ThemeImportantFactorsRequest();
        request.setIds(List.of(2L, 6L));

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/theme-disliked-factors")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("색상 저장")
    @Test
    void updateColor() throws Exception {

        // Given
        HintUsagePreferenceRequest request = new HintUsagePreferenceRequest();
        request.setId(4L);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/profiles/color")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }
}
