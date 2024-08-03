package com.sevenstars.roome.global.crawler.service;

import com.sevenstars.roome.domain.review.entity.Theme;
import com.sevenstars.roome.domain.review.service.ThemeService;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.common.response.Result;
import com.sevenstars.roome.global.crawler.request.CrawlingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrawlingService {

    private static final String SERVER_URL = "https://colory.mooo.com/bba/catalogue";
    private final ApiService apiService;
    private final ThemeService themeService;
    @Value("${application.secret}")
    private String secret;

    public void updateThemes(CrawlingRequest request) {
        validateSecret(request.getSecret());
        Document document = apiService.get(SERVER_URL);
        List<Theme> themes = parse(document);
        themeService.saveThemes(themes);
    }

    private List<Theme> parse(Document document) {
        List<Theme> themes = new ArrayList<>();
        Elements themesInfos = document.getElementsByClass("themes-info");

        for (Element themesInfo : themesInfos) {
            Elements selections = themesInfo.getElementsByClass("select-area");

            for (Element selection : selections) {
                String area = selection.text();
                Element table = selection.nextElementSibling();

                if (table != null && "table".equals(table.tagName())) {
                    themes.addAll(processTable(area, table));
                }
            }
        }

        return themes;
    }

    private String getTextByClass(Element element, String className) {
        return element.getElementsByClass(className).stream()
                .findFirst()
                .map(Element::text)
                .orElse("");
    }

    private List<Theme> processTable(String area, Element table) {
        List<Theme> themes = new ArrayList<>();
        Elements tableRows = table.select("tbody tr");
        String storeName = "";

        for (Element tableRow : tableRows) {
            String name = getTextByClass(tableRow, "info-1");
            if (StringUtils.hasText(name)) {
                storeName = name;
            }
            String themeName = getTextByClass(tableRow, "info-2");
            String scoreText = getTextByClass(tableRow, "info-3");
            double score;
            try {
                score = Double.parseDouble(scoreText);
            } catch (Exception exception) {
                log.error("Score parse failed", exception);
                score = 0.0;
            }

            String difficulty = getTextByClass(tableRow, "info-4");
            String reviewCountText = getTextByClass(tableRow, "info-5");

            int reviewCount;
            try {
                reviewCount = Integer.parseInt(reviewCountText);
            } catch (Exception exception) {
                log.error("Review Count parse failed", exception);
                reviewCount = 0;
            }

            themes.add(new Theme(area, storeName, themeName, score, difficulty, reviewCount));
        }

        return themes;
    }

    private void validateSecret(String secret) {
        if (!this.secret.equals(secret)) {
            throw new CustomClientErrorException(Result.INVALID_UPDATE_KEY);
        }
    }
}
