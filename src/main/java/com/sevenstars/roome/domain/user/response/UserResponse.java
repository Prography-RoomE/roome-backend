package com.sevenstars.roome.domain.user.response;

import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.entity.UserState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponse {
    private final UserState state;
    private final String email;
    private final String nickname;

    public static UserResponse from(User user) {
        return new UserResponse(user.getState(), user.getEmail(), user.getNickname());
    }
}
