package com.travplan.services.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AppleService extends _HttpService {

    @Value("${apple.client.id}")
    private String CLIENT_ID;

    @Value("${apple.login.redirect.url}")
    private String LOGIN_REDIRECT_URL;

    @Value("${apple.authorization.url}")
    private String AUTHORIZATION_URL;

    @Value("${apple.user.accesstoken.url}")
    private String ISSUE_ACCESS_TOKEN_URL;

    public String getAuthorizationUrl() {
        return urlBuilder(AUTHORIZATION_URL, params -> {
            params.put("client_id", URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8));
            params.put("response_type", URLEncoder.encode("code id_token", StandardCharsets.UTF_8));
            params.put("scope", URLEncoder.encode("name email", StandardCharsets.UTF_8));
            params.put("response_mode", URLEncoder.encode("form_post", StandardCharsets.UTF_8));
            params.put("redirect_url", URLEncoder.encode(LOGIN_REDIRECT_URL, StandardCharsets.UTF_8));
        });
    }

    public Response getAccessToken(String code) {
        return requestFormData(ISSUE_ACCESS_TOKEN_URL, params -> {
            params.put("client_id", URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8));
            params.put("client_secret", URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8));
            params.put("code", URLEncoder.encode(code, StandardCharsets.UTF_8));
            params.put("grant_type", URLEncoder.encode("authorization_code", StandardCharsets.UTF_8));
            params.put("redirect_url", URLEncoder.encode(LOGIN_REDIRECT_URL, StandardCharsets.UTF_8));
        });
    }
}
