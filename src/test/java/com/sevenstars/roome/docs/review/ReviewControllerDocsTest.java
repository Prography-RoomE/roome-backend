package com.sevenstars.roome.docs.review;

import com.sevenstars.roome.docs.RestDocsTest;
import com.sevenstars.roome.domain.review.controller.ReviewController;
import com.sevenstars.roome.domain.review.request.ReviewMandatoryRequest;
import com.sevenstars.roome.domain.review.request.ReviewOptionalRequest;
import com.sevenstars.roome.domain.review.response.*;
import com.sevenstars.roome.domain.review.service.ReviewService;
import com.sevenstars.roome.domain.review.service.ThemeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.sevenstars.roome.domain.review.entity.ReviewState.DRAFT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewControllerDocsTest extends RestDocsTest {

    private final ReviewService reviewService = mock(ReviewService.class);
    private final ThemeService themeService = mock(ThemeService.class);

    @Override
    protected Object initializeController() {
        return new ReviewController(reviewService, themeService);
    }

    @DisplayName("매장 이름 목록 조회")
    @Test
    void getStores() throws Exception {

        // Given
        StoresResponse response = new StoresResponse(List.of("제로월드 홍대점", "티켓 투 이스케이프", "오아시스"));

        given(themeService.getStores(any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(get("/reviews/stores")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .queryParam("storeName", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(queryParameters(parameterWithName("storeName").description("매장 이름(파라미터 없을 경우 전체 목록 조회)")),
                        responseFields(responseCommon())
                                .and(fieldWithPath("data.names[]").type(JsonFieldType.ARRAY)
                                        .description("매장 이름 목록"))));
    }

    @DisplayName("테마 이름 목록 조회")
    @Test
    void getThemes() throws Exception {

        // Given
        ThemesResponse response = new ThemesResponse(List.of("층간소음", "갤럭시 익스프레스", "배드 타임 (BÆD TIME)"));

        given(themeService.getThemes(any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(get("/reviews/themes")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .queryParam("name", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(queryParameters(parameterWithName("name").description("테마 이름(파라미터 없을 경우 전체 목록 조회)")),
                        responseFields(responseCommon())
                                .and(fieldWithPath("data.names[]").type(JsonFieldType.ARRAY)
                                        .description("테마 이름 목록"))));
    }

    @DisplayName("장르 목록 조회")
    @Test
    void getGenres() throws Exception {

        // Given
        List<GenresResponse.Genre> genres = List.of(
                new GenresResponse.Genre(1L, "액션"),
                new GenresResponse.Genre(2L, "범죄"),
                new GenresResponse.Genre(3L, "SF"),
                new GenresResponse.Genre(4L, "코미디"),
                new GenresResponse.Genre(5L, "로맨스"));

        GenresResponse response = new GenresResponse(genres);

        given(reviewService.getGenres())
                .willReturn(response);

        // When & Then
        mockMvc.perform(get("/reviews/genres")
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(responseFields(responseCommon())
                        .and(fieldWithPath("data.genres[]").type(JsonFieldType.ARRAY)
                                        .description("장르 목록"),
                                fieldWithPath("data.genres[].id").type(JsonFieldType.NUMBER)
                                        .description("장르 ID"),
                                fieldWithPath("data.genres[].title").type(JsonFieldType.STRING)
                                        .description("장르 제목"))));
    }

    @DisplayName("후기 목록 조회")
    @Test
    void getReviews() throws Exception {

        // Given
        List<ReviewsResponse.Review> reviews = List.of(new ReviewsResponse.Review(1L,
                        DRAFT,
                        4.5,
                        "제로월드 홍대점",
                        "층간소음",
                        List.of(new ReviewsResponse.Review.Genre(1L, "액션"),
                                new ReviewsResponse.Review.Genre(2L, "범죄"))),

                new ReviewsResponse.Review(2L,
                        DRAFT,
                        4.5,
                        "티켓 투 이스케이프",
                        "갤럭시 익스프레스",
                        Collections.emptyList()));

        ReviewsResponse response = new ReviewsResponse(3, 0, 2, 5L, reviews);

        given(reviewService.getReviews(any(), any(), any(), any(), any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(get("/reviews")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .queryParam("state", "draft")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sort", "id,asc")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(queryParameters(parameterWithName("state").description("후기 상태(draft, published)"),
                                parameterWithName("page").description("페이지(0부터 시작)"),
                                parameterWithName("size").description("한 페이지 원소 개수(최소 1 ~ 최대 100, 기본 값 10)"),
                                parameterWithName("sort").description("정렬('id,asc', 'id,desc', 'score,asc', 'score,desc', 'storeName,asc', 'storeName,desc', 'themeName,asc', 'themeName,desc')")),
                        responseFields(responseCommon())
                                .and(fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                                                .description("전체 페이지 수"),
                                        fieldWithPath("data.pageNumber").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지"),
                                        fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER)
                                                .description("한 페이지 원소 개수"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                                                .description("전체 원소 개수"),
                                        fieldWithPath("data.reviews[]").type(JsonFieldType.ARRAY)
                                                .description("후기 목록"),
                                        fieldWithPath("data.reviews[].id").type(JsonFieldType.NUMBER)
                                                .description("후기 ID"),
                                        fieldWithPath("data.reviews[].state").type(JsonFieldType.STRING)
                                                .description("상태"),
                                        fieldWithPath("data.reviews[].score").type(JsonFieldType.NUMBER)
                                                .description("점수"),
                                        fieldWithPath("data.reviews[].storeName").type(JsonFieldType.STRING)
                                                .description("매장 이름"),
                                        fieldWithPath("data.reviews[].themeName").type(JsonFieldType.STRING)
                                                .description("테마 이름"),
                                        fieldWithPath("data.reviews[].genres[]").type(JsonFieldType.ARRAY)
                                                .description("장르 목록"),
                                        fieldWithPath("data.reviews[].genres[].id").type(JsonFieldType.NUMBER)
                                                .description("장르 ID"),
                                        fieldWithPath("data.reviews[].genres[].title").type(JsonFieldType.STRING)
                                                .description("장르 제록"))));
    }

    @DisplayName("후기 조회")
    @Test
    void getReview() throws Exception {

        // Given
        ReviewResponse response = new ReviewResponse(1L,
                DRAFT,
                4.5,
                "제로월드 홍대점",
                "층간소음",
                false,
                false,
                false,
                LocalDate.of(2024, 8, 2),
                60,
                10,
                5,
                4,
                "어려움",
                "약함",
                "약함",
                4.0,
                4.5,
                5.0,
                "재밌어요.",
                List.of(new ReviewResponse.Genre(1L, "액션"),
                        new ReviewResponse.Genre(2L, "범죄")),
                Collections.emptyList());

        given(reviewService.getReview(any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(get("/reviews/{reviewId}", 1L)
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(pathParameters(parameterWithName("reviewId").description("후기 ID")),
                        responseFields(responseCommon())
                                .and(fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                                .description("후기 ID"),
                                        fieldWithPath("data.state").type(JsonFieldType.STRING)
                                                .description("상태"),
                                        fieldWithPath("data.score").type(JsonFieldType.NUMBER)
                                                .description("점수"),
                                        fieldWithPath("data.storeName").type(JsonFieldType.STRING)
                                                .description("매장 이름"),
                                        fieldWithPath("data.themeName").type(JsonFieldType.STRING)
                                                .description("테마 이름"),
                                        fieldWithPath("data.spoiler").type(JsonFieldType.BOOLEAN)
                                                .description("스포일러 유무"),
                                        fieldWithPath("data.isPublic").type(JsonFieldType.BOOLEAN)
                                                .description("공개 여부"),
                                        fieldWithPath("data.success").type(JsonFieldType.BOOLEAN)
                                                .description("성공 여부"),
                                        fieldWithPath("data.playDate").type(JsonFieldType.STRING)
                                                .description("플레이 일자(yyyy-MM-dd)"),
                                        fieldWithPath("data.totalTime").type(JsonFieldType.NUMBER)
                                                .description("총 시간"),
                                        fieldWithPath("data.remainingTime").type(JsonFieldType.NUMBER)
                                                .description("남은 시간"),
                                        fieldWithPath("data.hintCount").type(JsonFieldType.NUMBER)
                                                .description("사용 힌트 수"),
                                        fieldWithPath("data.participants").type(JsonFieldType.NUMBER)
                                                .description("참여 인원 수"),
                                        fieldWithPath("data.difficultyLevel").type(JsonFieldType.STRING)
                                                .description("체감 난이도"),
                                        fieldWithPath("data.fearLevel").type(JsonFieldType.STRING)
                                                .description("공포도"),
                                        fieldWithPath("data.activityLevel").type(JsonFieldType.STRING)
                                                .description("활동성"),
                                        fieldWithPath("data.interiorRating").type(JsonFieldType.NUMBER)
                                                .description("인테리어 점수"),
                                        fieldWithPath("data.directionRating").type(JsonFieldType.NUMBER)
                                                .description("연출 점수"),
                                        fieldWithPath("data.storyRating").type(JsonFieldType.NUMBER)
                                                .description("스토리 점수"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING)
                                                .description("후기 내용"),
                                        fieldWithPath("data.genres[]").type(JsonFieldType.ARRAY)
                                                .description("장르 목록"),
                                        fieldWithPath("data.genres[].id").type(JsonFieldType.NUMBER)
                                                .description("장르 ID"),
                                        fieldWithPath("data.genres[].title").type(JsonFieldType.STRING)
                                                .description("장르 제록"),
                                        fieldWithPath("data.imageUrls[]").type(JsonFieldType.ARRAY)
                                                .description("이미지 URL 목록"))));
    }

    @DisplayName("후기 생성")
    @Test
    void createReview() throws Exception {

        // Given
        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(4.5);
        request.setStoreName("제로월드 홍대점");
        request.setThemeName("층간소음");
        request.setGenreIds(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/reviews/")
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(responseFields(responseCommon())
                        .and(fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                .description("후기 ID"))));
    }

    @DisplayName("후기 필수 항목 업데이트")
    @Test
    void updateMandatoryContents() throws Exception {

        // Given
        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(4.5);
        request.setStoreName("제로월드 홍대점");
        request.setThemeName("층간소음");
        request.setGenreIds(Collections.emptyList());

        // When & Then
        mockMvc.perform(put("/reviews/{reviewId}/mandatory", 1L)
                        .header(AUTHORIZATION, "Bearer {token}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(pathParameters(parameterWithName("reviewId").description("후기 ID")),
                        responseFields(responseCommon())));
    }

    @DisplayName("후기 선택 항목 업데이트")
    @Test
    void updateOptionalContents() throws Exception {

        // Given
        byte[] content = "Hello, World!".getBytes();

        List<MockMultipartFile> multipartFiles = List.of(new MockMultipartFile("file", "1.png", "image/png", content),
                new MockMultipartFile("file", "2.png", "image/png", content),
                new MockMultipartFile("file", "3.png", "image/png", content),
                new MockMultipartFile("file", "4.png", "image/png", content),
                new MockMultipartFile("file", "5.png", "image/png", content));

        ReviewOptionalRequest reviewOptionalRequest = new ReviewOptionalRequest();
        reviewOptionalRequest.setSuccess(false);
        reviewOptionalRequest.setPlayDate(LocalDate.of(2024, 8, 2));
        reviewOptionalRequest.setTotalTime(60);
        reviewOptionalRequest.setRemainingTime(10);
        reviewOptionalRequest.setHintCount(1);
        reviewOptionalRequest.setParticipants(3);
        reviewOptionalRequest.setDifficultyLevel("보통");
        reviewOptionalRequest.setFearLevel("약함");
        reviewOptionalRequest.setActivityLevel("약함");
        reviewOptionalRequest.setInteriorRating(5.0);
        reviewOptionalRequest.setDirectionRating(4.5);
        reviewOptionalRequest.setStoryRating(4.0);
        reviewOptionalRequest.setContent("무난함");
        reviewOptionalRequest.setImageUrls(Collections.emptyList());
        reviewOptionalRequest.setSpoiler(true);
        reviewOptionalRequest.setIsPublic(false);

        String data = objectMapper.writeValueAsString(reviewOptionalRequest);

        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", data.getBytes());

        // When & Then
        mockMvc.perform(multipart("/reviews/{reviewId}/optional", 1L)
                        .file(multipartFiles.get(0))
                        .file(multipartFiles.get(1))
                        .file(multipartFiles.get(2))
                        .file(multipartFiles.get(3))
                        .file(multipartFiles.get(4))
                        .file(jsonFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("reviewId").description("후기 ID")),
                        requestParts(partWithName("file").description("이미지 파일"),
                                partWithName("json").description("JSON 데이터")),
                        requestPartFields("json", fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부").optional(),
                                fieldWithPath("playDate").type(JsonFieldType.STRING).description("플레이 일자(yyyy-MM-dd)").optional(),
                                fieldWithPath("totalTime").type(JsonFieldType.NUMBER).description("총 시간").optional(),
                                fieldWithPath("remainingTime").type(JsonFieldType.NUMBER).description("남은 시간").optional(),
                                fieldWithPath("hintCount").type(JsonFieldType.NUMBER).description("사용 힌트 수").optional(),
                                fieldWithPath("participants").type(JsonFieldType.NUMBER).description("참여 인원 수").optional(),
                                fieldWithPath("difficultyLevel").type(JsonFieldType.STRING).description("체감 난이도").optional(),
                                fieldWithPath("fearLevel").type(JsonFieldType.STRING).description("공포도").optional(),
                                fieldWithPath("activityLevel").type(JsonFieldType.STRING).description("활동성").optional(),
                                fieldWithPath("interiorRating").type(JsonFieldType.NUMBER).description("인테리어 점수").optional(),
                                fieldWithPath("directionRating").type(JsonFieldType.NUMBER).description("연출 점수").optional(),
                                fieldWithPath("storyRating").type(JsonFieldType.NUMBER).description("스토리 점수").optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("후기 내용").optional(),
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록").optional(),
                                fieldWithPath("spoiler").type(JsonFieldType.BOOLEAN).description("스포일러 유무").optional(),
                                fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부").optional()
                        ),
                        responseFields(responseCommon())
                                .and(fieldWithPath("data.imageUrls[]").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 목록"))));
    }

    @DisplayName("후기 등록")
    @Test
    void publishReview() throws Exception {

        // Given

        // When & Then
        mockMvc.perform(post("/reviews/{reviewId}/publish", 1L)
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(pathParameters(parameterWithName("reviewId").description("후기 ID")),
                        responseFields(responseCommon())));
    }

    @DisplayName("후기 삭제")
    @Test
    void deleteReview() throws Exception {

        // Given

        // When & Then
        mockMvc.perform(delete("/reviews/{reviewId}", 1L)
                        .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(pathParameters(parameterWithName("reviewId").description("후기 ID")),
                        responseFields(responseCommon())));
    }
}
