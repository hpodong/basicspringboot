package com.basicspringboot.services.api;

import com.basicspringboot.dto.FcmMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FirebaseService {

    @Value("${firebase.service.key}")
    private String serviceKey;

    @Autowired
    private ObjectMapper objectMapper;

    public Response pushNotification(String targetToken, String title, String body) throws IOException {

        String firebaseConfigPath = "firebase/"+serviceKey+".json";
        final InputStream inputStream = new ClassPathResource(firebaseConfigPath).getInputStream();

        final Map<String, String> jsonData = objectMapper.readValue(inputStream, Map.class);
        final String projectId = jsonData.get("project_id");

        final String message = makeMessage(targetToken, title, body);
        final OkHttpClient client = new OkHttpClient();
        final RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        final Request request = new Request.Builder()
                .url(factoryPushURL(projectId))
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        return client.newCall(request).execute();
    }

    private String getAccessToken() {
        String firebaseConfigPath = "firebase/"+serviceKey+".json";
        try {
            final GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeMessage(String targetToken, String title, String body) {
        return makeMessage(targetToken, title, body, null);
    }

    private String makeMessage(String targetToken, String title, String body, String image) {

        final FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(image)
                                .build()
                        ).build()).validateOnly(false).build();
        try {
            return objectMapper.writeValueAsString(fcmMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String factoryPushURL(String projectId) {
        return "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
    }
}
