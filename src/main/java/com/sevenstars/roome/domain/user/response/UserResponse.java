package com.sevenstars.roome.domain.user.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponse {
    private final String email;
    private final String nickname;
}
