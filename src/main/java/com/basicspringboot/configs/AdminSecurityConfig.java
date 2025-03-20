package com.basicspringboot.configs;

import com.basicspringboot.handlers.BasicAuthenticationFailureHandler;
import com.basicspringboot.models.admin.AdminMenu;
import com.basicspringboot.providers.AdminAuthenticationProvider;
import com.basicspringboot.services.manage.AdminConnectLogService;
import com.basicspringboot.services.manage.AdminMenuService;
import com.basicspringboot.services.manage.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AdminSecurityConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AdminService adminService;
    private final AdminMenuService adminMenuService;
    private final AdminConnectLogService connectLogService;

    private static final String[] ALWAYS_ALLOW_URLS = {
            "/admin",
            "/admin/css/**",
            "/admin/js/**",
            "/admin/images/**",
            "/plugins/**",
            "/uploads/**",
            "/favicon.png",
            "/admin/recent/**"
    };

    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        final List<AdminMenu> menus = adminMenuService.getAllPageMenus();
        log.info("MENUS : {}", menus);
        http
                .securityMatcher("/admin/**")
                .authenticationProvider(authenticationProvider())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable) // 필요 시 CSRF 활성화 가능
                .exceptionHandling(handler -> handler.accessDeniedHandler(accessDeniedHandler()))
                .authorizeHttpRequests(auth -> {
                            auth
                                    .requestMatchers(ALWAYS_ALLOW_URLS).permitAll()
                                    .requestMatchers("/admin/login").anonymous();

                            menus.forEach(menu -> {
                                final String link = menu.getLink();
                                auth
                                        .requestMatchers(link+"/**")
                                        .hasRole(menu.linkToRole());
                                log.info("LINK : {}, AUTH : {}", link+"/**", menu.linkToRole());

                            });

                            auth
                                    .requestMatchers("/admin/error/**")
                                    .authenticated();
                        }
                )
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
    public AdminAuthenticationProvider authenticationProvider() {
        return new AdminAuthenticationProvider(bCryptPasswordEncoder, adminService, adminMenuService, connectLogService);
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/admin/login");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> response.sendRedirect("/admin/error/access-denied");
    }
}

