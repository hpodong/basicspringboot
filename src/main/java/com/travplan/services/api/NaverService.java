package com.travplan.services.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class NaverService extends _HttpService {

    @Value("${naver.client.id}")
    private String CLIENT_ID;

    @Value("${naver.client.secret}")
    private String CLIENT_SECRET;

    @Value("${naver.authorization.url}")
    private String AUTHORIZATION_URL;

    @Value("${naver.user.accesstoken.url}")
    private String ACCESS_TOKEN_URL;

    @Value("${naver.user.info.url}")
    private String USER_INFO_URL;

    @Value("${naver.login.redirect.url}")
    private String LOGIN_REDIRECT_URL;

    @Value("${api.key}")
    private String API_KEY;

    public String getAuthorizationUrl() {
        return urlBuilder(AUTHORIZATION_URL, params -> {
            params.put("client_id", URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8));
            params.put("response_type", URLEncoder.encode("code", StandardCharsets.UTF_8));
            params.put("redirect_url", LOGIN_REDIRECT_URL);
            params.put("state", URLEncoder.encode(API_KEY, StandardCharsets.UTF_8));
        });
    }

    public Response getAccessToken(String code) {
        return requestFormData(ACCESS_TOKEN_URL, params -> {
            params.put("client_id", URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8));
            params.put("client_secret", URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8));
            params.put("code", URLEncoder.encode(code, StandardCharsets.UTF_8));
            params.put("grant_type", URLEncoder.encode("authorization_code", StandardCharsets.UTF_8));
            params.put("state", URLEncoder.encode(API_KEY, StandardCharsets.UTF_8));
        });
    }

    public Response getUserInfo(String accessToken) {
        return requestFormData(USER_INFO_URL, null, headers -> {
            headers.put("Authorization", "Bearer " + accessToken);
        });
    }
}
