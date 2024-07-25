package com.sevenstars.roome.global.version.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VersionType {

    SERVER("server"),
    AOS("aos"),
    IOS("ios");

    private final String name;
}
