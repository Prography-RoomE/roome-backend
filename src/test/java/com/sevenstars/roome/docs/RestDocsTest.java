package com.sevenstars.roome.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest {

    protected RestDocumentationResultHandler restDocs;
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.restDocs = resultHandler();
        this.mockMvc = MockMvcBuilders.standaloneSetup(initializeController())
                .apply(documentationConfiguration(provider).uris()
                        .withScheme("https")
                        .withHost("roome.site")
                        .withPort(443))
                .alwaysDo(print())
                .alwaysDo(restDocs) // 재정의한 핸들러(문서 디렉토리 명 설정과 pretty 패턴 설정) 적용
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
                .build();
    }

    protected abstract Object initializeController();

    protected FieldDescriptor[] responseCommon() {
        return new FieldDescriptor[]{
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 메세지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터").optional()
        };
    }

    private RestDocumentationResultHandler resultHandler() {
        return document("{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));
    }
}
