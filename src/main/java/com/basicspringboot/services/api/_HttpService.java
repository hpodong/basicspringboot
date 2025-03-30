package com.basicspringboot.services.api;

import com.basicspringboot.interfaces.ParamSetter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class _HttpService {

    protected Response requestFormData(String url, ParamSetter setter) {
        return requestFormData(url, setter, null);
    }

    protected Response requestFormData(String url, ParamSetter bodySetter, ParamSetter headerSetter) {
        final OkHttpClient client = new OkHttpClient();
        final Map<String, String> params = new HashMap<>();
        final Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.put(HttpHeaders.ACCEPT, org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        if(bodySetter != null) bodySetter.setData(params);
        if(headerSetter != null) headerSetter.setData(headers);
        final String requestData = mapToQueryString(params);

        if(requestData != null) {
            final RequestBody requestBody = RequestBody.create(requestData, MediaType.get("application/x-www-form-urlencoded; charset=utf-8"));
            final Request.Builder request = new Request.Builder().url(url).post(requestBody);

            headers.forEach(request::addHeader);

            try {
                return client.newCall(request.build()).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private String mapToQueryString(Map<String, String> params) {
        try {
            final StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!queryString.isEmpty()) {
                    queryString.append("&");
                }
                queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                queryString.append("=");
                queryString.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return queryString.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    protected String urlBuilder(String url, ParamSetter setter) {
        final Map<String, String> params = new HashMap<>();
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        setter.setData(params);
        params.forEach(builder::queryParam);
        return builder.toUriString();
    }
}
