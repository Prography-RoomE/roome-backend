package com.sevenstars.roome.docs.common;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.global.version.controller.VersionController;
import com.sevenstars.roome.global.version.request.VersionRequest;
import com.sevenstars.roome.global.version.request.VersionsRequest;
import com.sevenstars.roome.global.version.response.VersionResponse;
import com.sevenstars.roome.global.version.response.VersionsResponse;
import com.sevenstars.roome.global.version.service.VersionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VersionControllerDocsTest extends RestDocsTest {

    private final VersionService versionService = mock(VersionService.class);

    @Override
    protected Object initializeController() {
        return new VersionController(versionService);
    }

    @DisplayName("전체 버전 조회")
    @Test
    void getVersions() throws Exception {

        // Given
        String serverVersion = "1.0.0";
        String aosVersion = "1.0.1";
        String iosVersion = "1.0.2";

        given(versionService.getVersions())
                .willReturn(VersionsResponse.of(serverVersion, aosVersion, iosVersion));

        // When & Then
        mockMvc.perform(get("/versions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.serverVersion").type(JsonFieldType.STRING)
                                                .description("Server 버전"),
                                        fieldWithPath("data.aosVersion").type(JsonFieldType.STRING)
                                                .description("AOS 버전"),
                                        fieldWithPath("data.iosVersion").type(JsonFieldType.STRING)
                                                .description("iOS 버전"))));
    }

    @DisplayName("Server 버전 조회")
    @Test
    void getServerVersion() throws Exception {

        // Given
        String version = "1.0.0";
        given(versionService.getServerVersion())
                .willReturn(new VersionResponse(version));

        // When & Then
        mockMvc.perform(get("/versions/server"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.version").type(JsonFieldType.STRING)
                                                .description("Server 버전"))));
    }

    @DisplayName("AOS 버전 조회")
    @Test
    void getAosVersion() throws Exception {

        // Given
        String version = "1.0.0";
        given(versionService.getAosVersion())
                .willReturn(new VersionResponse(version));

        // When & Then
        mockMvc.perform(get("/versions/aos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.version").type(JsonFieldType.STRING)
                                                .description("AOS 버전"))));
    }

    @DisplayName("iOS 버전 조회")
    @Test
    void getIosVersion() throws Exception {

        // Given
        String version = "1.0.0";
        given(versionService.getIosVersion())
                .willReturn(new VersionResponse(version));

        // When & Then
        mockMvc.perform(get("/versions/ios"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())
                                .and(
                                        fieldWithPath("data.version").type(JsonFieldType.STRING)
                                                .description("iOS 버전"))));
    }

    @DisplayName("전체 버전 업데이트")
    @Test
    void updateVersions() throws Exception {

        // Given
        VersionsRequest request = new VersionsRequest();
        request.setServerVersion("1.0.0");
        request.setAosVersion("1.0.1");
        request.setIosVersion("1.0.2");
        request.setSecret("secret");

        // When & Then
        mockMvc.perform(put("/versions")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("Server 버전 업데이트")
    @Test
    void updateServerVersion() throws Exception {

        // Given
        VersionRequest request = new VersionRequest();
        request.setVersion("1.0.0");
        request.setSecret("secret");

        // When & Then
        mockMvc.perform(put("/versions/server")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("AOS 버전 업데이트")
    @Test
    void updateAosVersion() throws Exception {

        // Given
        VersionRequest request = new VersionRequest();
        request.setVersion("1.0.0");
        request.setSecret("secret");

        // When & Then
        mockMvc.perform(put("/versions/aos")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }

    @DisplayName("iOS 버전 업데이트")
    @Test
    void updateIosVersion() throws Exception {

        // Given
        VersionRequest request = new VersionRequest();
        request.setVersion("1.0.0");
        request.setSecret("secret");

        // When & Then
        mockMvc.perform(put("/versions/ios")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }
}
