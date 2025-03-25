package com.travplan.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travplan.models.ResponseModel;
import com.travplan.providers.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String[] ALWAYS_ALLOW_URLS = {
            "/api/app/auth",
            "/api/app/version/check",
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getRequestURI();
        return Arrays.stream(ALWAYS_ALLOW_URLS).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            final String token = bearerToken.substring(7);
            final Long idx = jwtProvider.getIdx(token);
            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(idx, null);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            final ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(response.getStatus());
            responseModel.setMessage("액세스 토큰이 없습니다.");
            responseModel.setError("NOT FOUND ACCESS TOKEN");

            response.getWriter().write(mapper.writeValueAsString(responseModel));
        }
    }
}
