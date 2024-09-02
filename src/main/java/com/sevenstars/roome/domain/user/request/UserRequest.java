package com.sevenstars.roome.domain.user.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserRequest {
    private final String serviceId;
    private final String serviceUserId;
    private final String email;
}
