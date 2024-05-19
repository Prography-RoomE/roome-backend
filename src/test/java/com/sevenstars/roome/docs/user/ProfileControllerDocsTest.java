package com.sevenstars.roome.docs.user;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.domain.profile.controller.ProfileController;
import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import com.sevenstars.roome.domain.profile.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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

    @DisplayName("프로필 기본 데이터 조회")
    @Test
    void getProfileDefaults() throws Exception {

        // Given
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
                new ProfileDefaultResponse.HintUsagePreference(2L, "최소한의 힌트만", "시간이 오래 걸릴 때만 써요"));

        List<ProfileDefaultResponse.DeviceLockPreference> deviceLockPreferences = List.of(
                new ProfileDefaultResponse.DeviceLockPreference(1L, "장치"),
                new ProfileDefaultResponse.DeviceLockPreference(2L, "자물쇠"));

        List<ProfileDefaultResponse.Activity> activities = List.of(
                new ProfileDefaultResponse.Activity(1L, "높은 활동성", "걷고, 뛰고, 계단 이동하는 게 좋아요"),
                new ProfileDefaultResponse.Activity(2L, "중간 활동성", "땀나지 않을 정도로 적당히 움직이는 게 좋아요"));

        List<ProfileDefaultResponse.DislikedFactor> dislikedFactors = List.of(
                new ProfileDefaultResponse.DislikedFactor(1L, "이과형 문제"),
                new ProfileDefaultResponse.DislikedFactor(2L, "노가다 문제"));

        List<ProfileDefaultResponse.Color> colors = List.of(
                new ProfileDefaultResponse.Color(1L, "색상 1"),
                new ProfileDefaultResponse.Color(2L, "색상 2"));

        ProfileDefaultResponse response = new ProfileDefaultResponse(genres,
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
                                        fieldWithPath("data.genres[]").type(JsonFieldType.ARRAY)
                                                .description("선호 장르 목록"),
                                        fieldWithPath("data.genres[].id").type(JsonFieldType.NUMBER)
                                                .description("선호 장르 ID"),
                                        fieldWithPath("data.genres[].title").type(JsonFieldType.STRING)
                                                .description("선호 장르 제목"),
                                        fieldWithPath("data.strengths[]").type(JsonFieldType.ARRAY)
                                                .description("본인 강점 목록"),
                                        fieldWithPath("data.strengths[].id").type(JsonFieldType.NUMBER)
                                                .description("본인 강점 ID"),
                                        fieldWithPath("data.strengths[].title").type(JsonFieldType.STRING)
                                                .description("본인 강점 제목"),
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
                                                .description("색상 제목"))));
    }
}
