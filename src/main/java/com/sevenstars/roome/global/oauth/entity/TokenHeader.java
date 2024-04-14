package com.sevenstars.roome.global.oauth.entity;

import lombok.Data;

@Data
public class TokenHeader {
    private String kid;
    private String alg;
}
