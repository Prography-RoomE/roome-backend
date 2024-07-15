package com.sevenstars.roome.docs.user;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.domain.profile.entity.Mbti;
import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.domain.user.controller.UserController;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.entity.UserState;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.response.UserImageResponse;
import com.sevenstars.roome.domain.user.response.UserResponse;
import com.sevenstars.roome.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sevenstars.roome.domain.profile.entity.ProfileState.COMPLETE;
import static com.sevenstars.roome.domain.profile.entity.color.ColorDirection.TOP_LEFT_TO_BOTTOM_RIGHT;
import static com.sevenstars.roome.domain.profile.entity.color.ColorMode.GRADIENT;
import static com.sevenstars.roome.domain.profile.entity.color.ColorShape.LINEAR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocsTest extends RestDocsTest {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initializeController() {
        return new UserController(userService);
    }

    @DisplayName("사용자 정보 조회")
    @Test
    void getUser() throws Exception {

        // Given
        User user = new User("google", "12345", "roome.dev@gmail.com");
        user.updateNickname("roome");
        user.updateImageUrl("https://s3.ap-northeast-2.amazonaws.com/roome-bucket/users/images/a9441fca-80b6-4f0f-9bc4-a4a03bf6f8ef.jpg");

        given(userService.getUser(any()))
                .willReturn(UserResponse.from(user));

        // When & Then
        mockMvc.perform(get("/users")
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.state").type(JsonFieldType.STRING)
                                                .description("상태(" + Arrays.stream(UserState.values())
                                                        .map(UserState::getValue)
                                                        .map(value -> "\"" + value + "\"")
                                                        .collect(Collectors.joining(", ")) + ")"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                                                .description("이미지 URL"))));
    }

    @DisplayName("약관 동의 저장")
    @Test
    void updateTermsAgreement() throws Exception {

        // Given
        TermsAgreementRequest request = new TermsAgreementRequest();
        request.setAgeOverFourteen(true);
        request.setServiceAgreement(true);
        request.setPersonalInfoAgreement(true);
        request.setMarketingAgreement(false);

        // When & Then
        mockMvc.perform(put("/users/terms-agreement")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("닉네임 유효성 검사")
    @Test
    void validateNickname() throws Exception {

        // Given
        NicknameRequest request = new NicknameRequest();
        request.setNickname("roome");

        // When & Then
        mockMvc.perform(post("/users/nickname/validation")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("닉네임 저장")
    @Test
    void updateNickname() throws Exception {

        // Given
        NicknameRequest request = new NicknameRequest();
        request.setNickname("roome");

        // When & Then
        mockMvc.perform(put("/users/nickname")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("이미지 저장")
    @Test
    void updateImage() throws Exception {

        // Given
        given(userService.updateImage(any(), any()))
                .willReturn(new UserImageResponse("https://s3.ap-northeast-2.amazonaws.com/roome-bucket/users/images/a9441fca-80b6-4f0f-9bc4-a4a03bf6f8ef.jpg"));

        // When & Then
        mockMvc.perform(multipart("/users/image")
                        .file("file", "example content".getBytes())
                        .header(AUTHORIZATION, "Bearer {token}")
                        .contentType(MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                                        .description("이미지 URL"))));
    }

    @DisplayName("이미지 삭제")
    @Test
    void deleteImage() throws Exception {

        // Given

        // When & Then
        mockMvc.perform(delete("/users/image")
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("프로필 정보 조회")
    @Test
    void getProfile() throws Exception {

        // Given
        List<ProfileResponse.Genre> genres = List.of(
                new ProfileResponse.Genre(9L, "미스터리", "미스터리"),
                new ProfileResponse.Genre(15L, "SF", "SF"));

        List<ProfileResponse.Strength> strengths = List.of(
                new ProfileResponse.Strength(4L, "집중력", "집중력"),
                new ProfileResponse.Strength(8L, "침착함", "침착함"));

        List<ProfileResponse.ImportantFactor> importantFactors = List.of(
                new ProfileResponse.ImportantFactor(3L, "명확한 개연성", "명확한 개연성"),
                new ProfileResponse.ImportantFactor(7L, "논리적인 문제", "논리적인 문제"));

        ProfileResponse.HorrorThemePosition horrorThemePosition = new ProfileResponse.HorrorThemePosition(4L, "마지모탱", "마지모탱");

        ProfileResponse.HintUsagePreference hintUsagePreference = new ProfileResponse.HintUsagePreference(2L, "최소한의 힌트만", "최소한의 힌트만");

        ProfileResponse.DeviceLockPreference deviceLockPreference = new ProfileResponse.DeviceLockPreference(1L, "장치", "장치");

        ProfileResponse.Activity activity = new ProfileResponse.Activity(3L, "낮은 활동성", "낮은 활동성");

        List<ProfileResponse.DislikedFactor> dislikedFactors = List.of(
                new ProfileResponse.DislikedFactor(2L, "노가다 문제", "노가다 문제"),
                new ProfileResponse.DislikedFactor(6L, "높은 난이도", "높은 난이도"));

        ProfileResponse.Color color = new ProfileResponse.Color(4L,
                "Gradient Purple",
                GRADIENT,
                LINEAR,
                TOP_LEFT_TO_BOTTOM_RIGHT,
                "#2E2E8D",
                "#73549D");

        ProfileResponse response = new ProfileResponse(
                "루미",
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

        given(userService.getUserProfile(any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(get("/users/profile")
                        .queryParam("nickname", "루미"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(parameterWithName("nickname").description("닉네임")),
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                                                .description("사용자 닉네임"),
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
                                        fieldWithPath("data.preferredGenres[].text").type(JsonFieldType.STRING)
                                                .description("선호 장르 텍스트"),
                                        fieldWithPath("data.mbti").type(JsonFieldType.STRING)
                                                .description("MBTI"),
                                        fieldWithPath("data.userStrengths[]").type(JsonFieldType.ARRAY)
                                                .description("사용자 강점 목록"),
                                        fieldWithPath("data.userStrengths[].id").type(JsonFieldType.NUMBER)
                                                .description("사용자 강점 ID"),
                                        fieldWithPath("data.userStrengths[].title").type(JsonFieldType.STRING)
                                                .description("사용자 강점 제목"),
                                        fieldWithPath("data.userStrengths[].text").type(JsonFieldType.STRING)
                                                .description("사용자 강점 텍스트"),
                                        fieldWithPath("data.themeImportantFactors[]").type(JsonFieldType.ARRAY)
                                                .description("테마 중요 요소 목록"),
                                        fieldWithPath("data.themeImportantFactors[].id").type(JsonFieldType.NUMBER)
                                                .description("테마 중요 요소 ID"),
                                        fieldWithPath("data.themeImportantFactors[].title").type(JsonFieldType.STRING)
                                                .description("테마 중요 요소 제목"),
                                        fieldWithPath("data.themeImportantFactors[].text").type(JsonFieldType.STRING)
                                                .description("테마 중요 요소 텍스트"),
                                        fieldWithPath("data.horrorThemePosition.id").type(JsonFieldType.NUMBER)
                                                .description("공포 테마 포지션 ID"),
                                        fieldWithPath("data.horrorThemePosition.title").type(JsonFieldType.STRING)
                                                .description("공포 테마 포지션 제목"),
                                        fieldWithPath("data.horrorThemePosition.text").type(JsonFieldType.STRING)
                                                .description("공포 테마 포지션 텍스트"),
                                        fieldWithPath("data.hintUsagePreference.id").type(JsonFieldType.NUMBER)
                                                .description("힌트 사용 선호도 ID"),
                                        fieldWithPath("data.hintUsagePreference.title").type(JsonFieldType.STRING)
                                                .description("힌트 사용 선호도 제목"),
                                        fieldWithPath("data.hintUsagePreference.text").type(JsonFieldType.STRING)
                                                .description("힌트 사용 선호도 텍스트"),
                                        fieldWithPath("data.deviceLockPreference.id").type(JsonFieldType.NUMBER)
                                                .description("장치 자물쇠 선호도 ID"),
                                        fieldWithPath("data.deviceLockPreference.title").type(JsonFieldType.STRING)
                                                .description("장치 자물쇠 선호도 제목"),
                                        fieldWithPath("data.deviceLockPreference.text").type(JsonFieldType.STRING)
                                                .description("장치 자물쇠 선호도 텍스트"),
                                        fieldWithPath("data.activity.id").type(JsonFieldType.NUMBER)
                                                .description("활동성 ID"),
                                        fieldWithPath("data.activity.title").type(JsonFieldType.STRING)
                                                .description("활동성 제목"),
                                        fieldWithPath("data.activity.text").type(JsonFieldType.STRING)
                                                .description("활동성 텍스트"),
                                        fieldWithPath("data.themeDislikedFactors[]").type(JsonFieldType.ARRAY)
                                                .description("싫어하는 요소 목록"),
                                        fieldWithPath("data.themeDislikedFactors[].id").type(JsonFieldType.NUMBER)
                                                .description("싫어하는 요소 ID"),
                                        fieldWithPath("data.themeDislikedFactors[].title").type(JsonFieldType.STRING)
                                                .description("싫어하는 요소 제목"),
                                        fieldWithPath("data.themeDislikedFactors[].text").type(JsonFieldType.STRING)
                                                .description("싫어하는 요소 텍스트"),
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
}
