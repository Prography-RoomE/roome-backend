package com.sevenstars.roome.docs.auth;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.global.auth.controller.AuthController;
import com.sevenstars.roome.global.auth.request.DeactivateRequest;
import com.sevenstars.roome.global.auth.request.SignInRequest;
import com.sevenstars.roome.global.auth.request.TokenRequest;
import com.sevenstars.roome.global.auth.response.TokenResponse;
import com.sevenstars.roome.global.auth.service.LoginService;
import com.sevenstars.roome.global.jwt.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerDocsTest extends RestDocsTest {

    private final LoginService loginService = mock(LoginService.class);
    private final JwtTokenService tokenService = mock(JwtTokenService.class);

    @Override
    protected Object initializeController() {
        return new AuthController(loginService, tokenService);
    }

    @DisplayName("로그인")
    @Test
    void signIn() throws Exception {

        // Given
        TokenResponse response = new TokenResponse("Access Token", "Refresh Token");
        given(loginService.signIn(any()))
                .willReturn(response);

        SignInRequest request = new SignInRequest();
        request.setProvider("google");
        request.setCode("Authorization Code");
        request.setIdToken("Identity Token");

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/signin")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                                .description("액세스 토큰"),
                                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                                .description("리프레시 토큰"))));
    }

    @DisplayName("토큰 재발급")
    @Test
    void token() throws Exception {

        // Given
        TokenResponse response = new TokenResponse("Access Token", "Refresh Token");
        given(tokenService.getToken(any()))
                .willReturn(response);

        TokenRequest request = new TokenRequest();
        request.setRefreshToken("Refresh Token");

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                                .description("액세스 토큰"),
                                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                                .description("리프레시 토큰"))));
    }

    @DisplayName("로그아웃")
    @Test
    void signOut() throws Exception {

        // Given

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/signout")
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("탈퇴")
    @Test
    void deactivate() throws Exception {

        // Given
        DeactivateRequest request = new DeactivateRequest();
        request.setProvider("google");
        request.setCode("Authorization Code");
        request.setReason("원하는 기능이 없어요");
        request.setContent("방탈출 메이트 구하는 기능이 없어요!");

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/deactivate")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }
}
