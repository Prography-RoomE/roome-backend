package com.sevenstars.roome.global.jwt.config;

import com.sevenstars.roome.global.jwt.filter.JwtAuthorizationFilter;
import com.sevenstars.roome.global.jwt.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void configure(HttpSecurity http) {
        JwtAuthorizationFilter filter = new JwtAuthorizationFilter(tokenProvider);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
