package com.basicspringboot.configs;

import com.basicspringboot.enums.AdminStatus;
import com.basicspringboot.handlers.BasicAuthenticationFailureHandler;
import com.basicspringboot.providers.AdminAuthenticationProvider;
import com.basicspringboot.services.manage.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AdminService adminService;

    private static final String[] ALWAYS_ALLOW_URLS = {
            "/admin/css/**",
            "/admin/js/**",
            "/admin/images/**",
            "/plugins/**",
    };

    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .authenticationProvider(adminAuthenticationProvider())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable) // 필요 시 CSRF 활성화 가능
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALWAYS_ALLOW_URLS).permitAll()
                        .requestMatchers("/admin/login").anonymous()
                        .requestMatchers("/admin/**").hasRole(AdminStatus.ACTIVE.toString()).anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/admin/login")
                        .defaultSuccessUrl("/admin")
                        .failureHandler(new BasicAuthenticationFailureHandler())
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }


    @Bean
    public AdminAuthenticationProvider adminAuthenticationProvider() {
        return new AdminAuthenticationProvider(bCryptPasswordEncoder, adminService);
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/admin/login");
    }
}

