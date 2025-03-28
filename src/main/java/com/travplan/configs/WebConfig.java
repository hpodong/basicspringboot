package com.travplan.configs;

import com.travplan.converters.*;
import com.travplan.interceptors.web.AdminInterceptors;
import com.travplan.interceptors.web.FrontInterceptors;
import com.travplan.services.client.ClientPageService;
import com.travplan.services.contents.PopupService;
import com.travplan.services.manage.AdminMenuService;
import com.travplan.services.manage.AdminPushLogService;
import com.travplan.services.member.MemberService;
import com.travplan.services.visit.SEOService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.useragent}")
    private String APP_USERAGENT;

    private final StringToSocialTypeConverter stringToSocialTypeConverter;
    private final StringToAPStatusConverter stringToAPStatusConverter;

    private final PopupService popupService;
    private final AdminPushLogService adminPushLogService;
    private final AdminMenuService adminMenuService;
    private final ClientPageService clientPageService;
    private final SEOService seoService;
    private final MemberService memberService;

    @Override
    public void addInterceptors(InterceptorRegistry ir) {

        ir.addInterceptor(new AdminInterceptors(adminMenuService, adminPushLogService))
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/logout")
                .excludePathPatterns("/admin/css/**")
                .excludePathPatterns("/admin/js/**")
                .excludePathPatterns("/admin/plugins/**")
                .excludePathPatterns("/admin/images/**")
                .excludePathPatterns("/admin/error/**");

        ir.addInterceptor(new FrontInterceptors(clientPageService,
                        seoService,
                        popupService,
                        memberService,
                        APP_USERAGENT
                ))
                .addPathPatterns("", "/**")
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/api/**")
                .excludePathPatterns("/front/css/**", "/front/js/**", "/front/images/**")
                .excludePathPatterns("/front/images/**")
                .excludePathPatterns("/uploads/**", "/js/**", "/css/**", "/images/**", "/.well-known/**")
                .excludePathPatterns("/favicon.ico")
                .excludePathPatterns("/add/page-log");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        final CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.DAYS);
        registry.addResourceHandler("/uploads/**").addResourceLocations(getUploadDir());
        registry.addResourceHandler("/robots.txt").setCacheControl(cacheControl).addResourceLocations(getUploadDir("robots.txt"));
        registry.addResourceHandler("/sitemap.xml").setCacheControl(cacheControl).addResourceLocations(getUploadDir("sitemap.xml"));
        registry.addResourceHandler("/.well-known/pki-validation/**").setCacheControl(cacheControl).addResourceLocations("classpath:/.well-known/pki-validation/");
    }

    private String getUploadDir() {
        return getUploadDir(null);
    }

    private String getUploadDir(String filename) {
        if(filename != null) {
            return getUserDir() + "/uploads/" + filename;
        } else {
            return getUserDir() + "/uploads/";
        }
    }

    private String getUserDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String dir = System.getProperty("user.dir");
        if (os.contains("win")) {
            dir = "file:///" + dir.replace("\\", "/");  // 윈도우 경로 형식에 맞게 처리
        } else {
            dir = "file://" + dir;
        }
        return dir;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToSocialTypeConverter);
        registry.addConverter(stringToAPStatusConverter);
    }
}
