package com.sevenstars.roome.docs.common;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.global.common.controller.HealthCheckController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthCheckControllerDocsTest extends RestDocsTest {

    @Override
    protected Object initializeController() {
        return new HealthCheckController();
    }

    @DisplayName("헬스 체크")
    @Test
    void getHealth() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));

    }
}
