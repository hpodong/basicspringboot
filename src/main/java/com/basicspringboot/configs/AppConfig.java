package com.basicspringboot.configs;

import com.basicspringboot.interceptors.app.AppInterceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private AppInterceptors appInterceptors;

    @Override
    public void addInterceptors(InterceptorRegistry ir) {
        ir.addInterceptor(appInterceptors)
                .addPathPatterns("/api/app/**");
    }
}
