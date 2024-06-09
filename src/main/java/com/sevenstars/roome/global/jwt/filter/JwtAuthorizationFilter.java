package com.sevenstars.roome.global.jwt.filter;

import com.sevenstars.roome.global.jwt.service.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(AUTHORIZATION_HEADER_NAME);

        if (StringUtils.hasText(token)) {
            try {
                Claims claims = tokenProvider.verifyAccessToken(token);

                Long userId = Long.valueOf(claims.getSubject());

                Authentication authentication = tokenProvider.getAuthentication(userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception exception) {
                log.error("Token authentication failed, {}", exception.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
