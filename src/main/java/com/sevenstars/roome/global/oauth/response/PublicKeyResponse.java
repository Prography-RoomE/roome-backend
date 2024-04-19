package com.sevenstars.roome.global.oauth.response;

import lombok.Data;

import java.util.List;

@Data
public class PublicKeyResponse {
    private List<Key> keys;
}
