package com.sevenstars.roome.docs.crawler;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.global.crawler.controller.CrawlingController;
import com.sevenstars.roome.global.crawler.request.CrawlingRequest;
import com.sevenstars.roome.global.crawler.service.CrawlingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CrawlingControllerDocsTest extends RestDocsTest {

    private final CrawlingService crawlingService = mock(CrawlingService.class);

    @Override
    protected Object initializeController() {
        return new CrawlingController(crawlingService);
    }

    @DisplayName("테마 정보 크롤링")
    @Test
    void updateThemes() throws Exception {

        // Given
        CrawlingRequest request = new CrawlingRequest();
        request.setSecret("secret key");

        // When & Then
        mockMvc.perform(post("/crawling/themes")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(responseCommon())));
    }
}
