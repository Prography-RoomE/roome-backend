package com.sevenstars.roome.global.oauth.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class Key {
    private final String kty;
    private final String kid;
    private final String use;
    private final String alg;
    private final String n;
    private final String e;
}
