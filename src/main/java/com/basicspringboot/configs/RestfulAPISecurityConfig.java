package com.basicspringboot.configs;

import com.basicspringboot.providers.RestfulAPIAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestfulAPISecurityConfig {

    private static final String[] ALWAYS_ALLOW_URLS = {
            "/api/login"
    };

    @Value("${api.key}")
    private String API_KEY;

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(authFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALWAYS_ALLOW_URLS).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public RestfulAPIAuthenticationFilter authFilter() {
        return new RestfulAPIAuthenticationFilter(API_KEY);
    }
}

