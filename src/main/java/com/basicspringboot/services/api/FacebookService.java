package com.basicspringboot.services.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class FacebookService extends _HttpService {

    @Value("${facebook.app.id}")
    private String APP_ID;

    @Value("${facebook.app.secret}")
    private String APP_SECRET;

    @Value("${facebook.authorization.url}")
    private String AUTHORIZATION_URL;

    @Value("${facebook.accesstoken.url}")
    private String ACCESS_TOKEN_URL;

    @Value("${facebook.user.info.url}")
    private String USER_INFO_URL;

    @Value("${facebook.login.redirect.url}")
    private String LOGIN_REDIRECT_URL;

    public String getAuthorizationUrl() {
        return urlBuilder(AUTHORIZATION_URL, params -> {
            params.put("client_id", URLEncoder.encode(APP_ID, StandardCharsets.UTF_8));
            params.put("redirect_url", LOGIN_REDIRECT_URL);
        });
    }

    public Response getAccessToken(String code) {
        return requestFormData(ACCESS_TOKEN_URL, params -> {
            params.put("client_id", URLEncoder.encode(APP_ID, StandardCharsets.UTF_8));
            params.put("client_secret", URLEncoder.encode(APP_SECRET, StandardCharsets.UTF_8));
            params.put("code", URLEncoder.encode(code, StandardCharsets.UTF_8));
            params.put("redirect_url", LOGIN_REDIRECT_URL);
        });
    }

    public Response getUserInfo(String accessToken) {
        return requestFormData(USER_INFO_URL, params -> {
            params.put("fields", URLEncoder.encode("id,name,email", StandardCharsets.UTF_8));
            params.put("access_token", URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
        });
    }
}
