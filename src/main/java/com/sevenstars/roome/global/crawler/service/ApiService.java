package com.sevenstars.roome.global.crawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApiService {

    @Retryable(retryFor = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 30000))
    public Document get(String url) {
        log.info("Get Request URL: {}", url);

        Connection connection = Jsoup.connect(url);

        try {
            return connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
