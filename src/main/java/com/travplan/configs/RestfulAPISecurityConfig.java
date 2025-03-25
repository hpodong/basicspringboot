package com.travplan.configs;

import com.travplan.dto.ResponseDTO;
import com.travplan.filters.JWTFilter;
import com.travplan.interfaces.ResponseSetter;
import com.travplan.providers.JwtProvider;
import com.travplan.providers.RestfulAPIAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestfulAPISecurityConfig {

    @Value("${api.key}")
    private String API_KEY;

    private final JwtProvider jwtProvider;

    @Bean
    @Order(0)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/app/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(new RestfulAPIAuthenticationFilter(API_KEY), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()
                );
        return http.build();
    }
}

