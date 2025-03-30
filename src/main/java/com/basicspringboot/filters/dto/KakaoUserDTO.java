package com.basicspringboot.filters.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class KakaoUserDTO {
    private Long id;
    private Timestamp connected_at;
    private KakaoUserProperties properties;
    private KakaoAccount kakao_account;

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    public static class KakaoUserProperties {
        private String profile_image;
        private String thumbnail_image;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    public static class KakaoAccount {
        private Boolean profile_image_needs_agreement;
        private KakaoAccountProfile profile;
        private Boolean has_email;
        private Boolean email_needs_agreement;
        private Boolean is_email_valid;
        private Boolean is_email_verified;
        private String email;
        private Boolean has_birthday;
        private Boolean birthday_needs_agreement;
        private String birthday;
        private String birthday_type;
        private Boolean has_gender;
        private Boolean gender_needs_agreement;
        private String gender;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    public static class KakaoAccountProfile {
        private String thumbnail_image_url;
        private String profile_image_url;
        private Boolean is_default_image;
    }
}
