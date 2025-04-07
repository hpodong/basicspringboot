package com.basicspringboot.services.api;

import com.basicspringboot.dto.AligoAuthNumber;
import com.basicspringboot.exceptions.APIException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Slf4j
public class AligoService {
    private static final String BASE_URL = "https://apis.aligo.in/send";
    private static final String SEND_AUTH_NUMBER_TITLE = "인증번호입니다.";
    private static final String SEND_AUTH_NUMBER_DESCRIPTION = "인증번호 [%인증번호%]을 입력해주세요.";

    private final Set<AligoAuthNumber> authNumbers = new CopyOnWriteArraySet<>();

    @Value("${aligo.identifier}")
    private String IDENTIFIER;
    @Value("${aligo.key}")
    private String API_KEY;
    @Value("${aligo.sender}")
    private String SENDER;
    @Value("${aligo.testmode}")
    private String TEST_MODE;

    private AligoAuthNumber findAuthNumber(String target) {
        return authNumbers.stream().filter(number -> number.getCell().equals(target)).findFirst().orElse(null);
    }

    public Response sendAuthNumber(String target) throws IOException {
        authNumbers.remove(findAuthNumber(target));
        final String randomCode = generateVerificationCode();
        final LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(3);
        authNumbers.add(new AligoAuthNumber(target, randomCode, expiredAt));
        return sendMessage(target, SEND_AUTH_NUMBER_TITLE, SEND_AUTH_NUMBER_DESCRIPTION, randomCode);
    }

    public void verifyAuthNumber(String target, String authNumber) {
        final LocalDateTime now = LocalDateTime.now();
        final AligoAuthNumber data = findAuthNumber(target);
        if(data == null) throw new APIException("인증번호를 찾을 수 없습니다.");
        else if(data.getExpiredAt().isBefore(now)) throw new APIException("인증 시간이 지났습니다. 인증을 다시 진행해주세요");
        else if(!data.getAuthNumber().equals(authNumber)) throw new APIException("인증 번호가 일치하지 않습니다");
        authNumbers.remove(data);
    }

    private Response sendMessage(String target, String title, String description, String... destination) throws IOException {

        final OkHttpClient client = new OkHttpClient();
        final RequestBody requestBody = new FormBody.Builder()
                .add("key", API_KEY)
                .add("user_id", IDENTIFIER)
                .add("sender", SENDER)
                .add("receiver", target)
                .add("destination", String.join("|", destination))
                .add("title", title)
                .add("msg", description)
                .add("testmode_yn", TEST_MODE)
                .build();
        final Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    private String generateVerificationCode() {
        final Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
