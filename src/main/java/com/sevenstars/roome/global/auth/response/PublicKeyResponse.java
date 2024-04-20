package com.sevenstars.roome.global.auth.response;

import lombok.Data;

import java.util.List;

@Data
public class PublicKeyResponse {
    private List<Key> keys;
}
