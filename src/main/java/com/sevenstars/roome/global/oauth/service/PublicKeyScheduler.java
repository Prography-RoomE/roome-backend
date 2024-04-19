package com.sevenstars.roome.global.oauth.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PublicKeyScheduler {

    private final List<AbstractLoginService> loginServices;

    @Value("${schedule.enabled}")
    private boolean scheduleEnabled;

    @PostConstruct
    public void init() {
        update();
    }

    @Scheduled(cron = "${schedule.cron}")
    public void update() {

        if (!scheduleEnabled) {
            return;
        }

        for (AbstractLoginService loginService : loginServices) {
            try {
                log.info("Initiates {} public key update.", loginService.getProvider().getName());
                loginService.updateKey();
            } catch (Exception exception) {
                log.error("{}", exception.getMessage());
            }
        }
    }
}
