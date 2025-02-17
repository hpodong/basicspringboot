package com.basicspringboot.services.api;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class KakaoService extends _HttpService{

    @Value("${spring.application.name}")
    private String COMPANY_NAME;

    @Value("${kakao.aligo.api.url}")
    private String API_URL;

    @Value("${kakao.aligo.api.key}")
    private String API_KEY;

    @Value("${kakao.client.secret}")
    private String CLIENT_SECRET;

    @Value("${kakao.aligo.user.id}")
    private String USER_ID;

    @Value("${kakao.aligo.sender.number}")
    private String SENDER_NUMBER;

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

    @Value("${kakao.aligo.is.test}")
    private boolean IS_TEST;

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

    public Response sendCertificateNumber(String phoneNumber) {

        final String code = generateVerificationCode();
        final String msg = "["+COMPANY_NAME+"] 본인확인 인증번호\n["+code+"]를 화면에 입력해주세요.";

        return requestFormData(API_URL, params -> {
            params.put("key", API_KEY);
            params.put("user_id", USER_ID);
            params.put("sender", SENDER_NUMBER);
            params.put("receiver", phoneNumber);
            params.put("msg", msg);
            if(IS_TEST) params.put("testmode_yn", "Y");
        });
    }

    public String generateVerificationCode() {
        final Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
