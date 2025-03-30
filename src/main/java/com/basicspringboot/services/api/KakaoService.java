package com.basicspringboot.services.api;

import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class KakaoService extends _HttpService {

    @Value("${spring.application.name}")
    private String COMPANY_NAME;

    @Value("${kakao.client.secret}")
    private String CLIENT_SECRET;

    @Value("${kakao.authorization.url}")
    private String AUTHORIZATION_URL;

    @Value("${kakao.user.accesstoken.url}")
    private String ISSUE_ACCESS_TOKEN_URL;

    @Value("${kakao.user.info.url}")
    private String USER_INFO_URL;

    @Value("${kakao.rest.api.key}")
    private String REST_API_KEY;

    @Value("${kakao.login.redirect.url}")
    private String LOGIN_REDIRECT_URL;

    public Response getAuthorizationCode() {
        return requestFormData(AUTHORIZATION_URL, params -> {
            params.put("client_id", REST_API_KEY);
            params.put("grant_type", "authorization_code");
            params.put("redirect_uri", LOGIN_REDIRECT_URL);
            params.put("response_type", "code");
        });
    }

    public Response getAccessToken(String code) {
        return requestFormData(ISSUE_ACCESS_TOKEN_URL, params -> {
            params.put("grant_type", "authorization_code");
            params.put("client_id", REST_API_KEY);
            params.put("redirect_uri", LOGIN_REDIRECT_URL);
            params.put("client_secret", CLIENT_SECRET);
            params.put("code", code);
        });
    }

    public Response getUserInfo(String accessToken) {
        return requestFormData(USER_INFO_URL, null, headers -> headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
    }
}
