package com.sevenstars.roome.docs.user;

import com.sevenstars.roome.docs.RestDocsTest;
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
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
}
