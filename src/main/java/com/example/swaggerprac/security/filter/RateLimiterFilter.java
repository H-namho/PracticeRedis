package com.example.swaggerprac.security.filter;

import com.example.swaggerprac.redis.RateLimiter;
import com.example.swaggerprac.security.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return path.startsWith("/api/user/login")
                || path.startsWith("/api/user/signup")
                || path.startsWith("/api/user/refresh")
                || path.startsWith("/api/user/me")
                || path.startsWith("/api/user/members")
                || path.startsWith("/api/room/myroom")
                || path.startsWith("/readMessage")
                || ("GET".equalsIgnoreCase(method) && path.startsWith("/api/post"))
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = authHeader.substring("Bearer ".length());

        try {
            Long userId = jwtUtil.extractUserId(accessToken);

            if (!rateLimiter.allow(userId)) {
                response.setStatus(429);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\":\"잠시 기다렸다가 요청해주세요.\"}");
                return;
            }
        } catch (JwtException | IllegalArgumentException exception) {
            // 인증 실패 판단은 JWT 필터와 Security가 담당하고, RateLimiter는 요청만 담당
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
