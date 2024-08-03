package com.sevenstars.roome.domain.review.response;

import com.sevenstars.roome.domain.profile.entity.Element;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GenresResponse {

    private final List<Genre> genres;

    public static GenresResponse from(List<Element> genres) {
        return new GenresResponse(genres.stream().map(Genre::from).toList());
    }

    @Getter
    @RequiredArgsConstructor
    public static class Genre {
        private final Long id;
        private final String title;

        public static Genre from(Element genre) {
            return new Genre(genre.getId(), genre.getTitle());
        }
    }
}
