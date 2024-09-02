package com.sevenstars.roome.global.version.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VersionsResponse {
    private final String serverVersion;
    private final String aosVersion;
    private final String iosVersion;

    public static VersionsResponse of(String serverVersion,
                                      String aosVersion,
                                      String iosVersion) {
        return new VersionsResponse(serverVersion, aosVersion, iosVersion);
    }
}
