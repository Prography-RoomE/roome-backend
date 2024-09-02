package com.sevenstars.roome.global.version.service;


import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.common.response.Result;
import com.sevenstars.roome.global.version.entity.Version;
import com.sevenstars.roome.global.version.entity.VersionType;
import com.sevenstars.roome.global.version.repository.VersionRepository;
import com.sevenstars.roome.global.version.request.VersionRequest;
import com.sevenstars.roome.global.version.request.VersionsRequest;
import com.sevenstars.roome.global.version.response.VersionResponse;
import com.sevenstars.roome.global.version.response.VersionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static com.sevenstars.roome.global.version.entity.VersionType.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VersionService {

    private final VersionRepository versionRepository;

    @Value("${application.secret}")
    private String secret;

    public VersionsResponse getVersions() {
        return new VersionsResponse(
                getVersionByType(SERVER),
                getVersionByType(AOS),
                getVersionByType(IOS)
        );
    }

    public VersionResponse getServerVersion() {
        return new VersionResponse(getVersionByType(SERVER));
    }

    public VersionResponse getAosVersion() {
        return new VersionResponse(getVersionByType(AOS));
    }

    public VersionResponse getIosVersion() {
        return new VersionResponse(getVersionByType(IOS));
    }

    @Transactional
    public void updateVersions(VersionsRequest request) {
        validateSecret(request.getSecret());
        updateVersion(SERVER, request::getServerVersion);
        updateVersion(AOS, request::getAosVersion);
        updateVersion(IOS, request::getIosVersion);
    }

    @Transactional
    public void updateServerVersion(VersionRequest request) {
        updateVersion(SERVER, request);
    }

    @Transactional
    public void updateAosVersion(VersionRequest request) {
        updateVersion(AOS, request);
    }

    @Transactional
    public void updateIosVersion(VersionRequest request) {
        updateVersion(IOS, request);
    }

    private void updateVersion(VersionType type, VersionRequest request) {
        validateSecret(request.getSecret());
        updateVersion(type, request::getVersion);
    }

    private void updateVersion(VersionType type, Supplier<String> versionSupplier) {
        String version = versionSupplier.get();
        versionRepository.findByType(type).ifPresentOrElse(
                existingVersion -> existingVersion.update(version),
                () -> versionRepository.save(new Version(type, version))
        );
    }

    private String getVersionByType(VersionType type) {
        return versionRepository.findByType(type).orElse(new Version(type, "")).getVersion();
    }

    private void validateSecret(String secret) {
        if (!this.secret.equals(secret)) {
            throw new CustomClientErrorException(Result.INVALID_UPDATE_KEY);
        }
    }
}
