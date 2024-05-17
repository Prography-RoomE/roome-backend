package com.sevenstars.roome.docs.user;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.domain.user.controller.UserController;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.entity.UserState;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.response.UserResponse;
import com.sevenstars.roome.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

        given(userService.getUser(any()))
                .willReturn(UserResponse.from(user));

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users")
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
                                                .description("닉네임"))));
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
        mockMvc.perform(RestDocumentationRequestBuilders.put("/users/terms-agreement")
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
        mockMvc.perform(RestDocumentationRequestBuilders.post("/users/nickname/validation")
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
        mockMvc.perform(RestDocumentationRequestBuilders.put("/users/nickname")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }
}
