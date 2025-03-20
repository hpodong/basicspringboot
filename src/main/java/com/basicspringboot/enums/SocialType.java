package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum SocialType implements BSEnum<SocialType> {
    KAKAO("K"),
    NAVER("N"),
    APPLE("A"),
    FACEBOOK("F"),
    GOOGLE("G");

    private final String value;

    SocialType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<SocialType> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public BSEnum<SocialType> enumFromName(String name) {
        return null;
    }

    @Override
    public String getClassName() {
        return switch (this) {
            case SocialType.KAKAO -> "sign-kakao";
            case SocialType.NAVER -> "sign-naver";
            case SocialType.APPLE -> "sign-apple";
            case SocialType.FACEBOOK -> "sign-facebook";
            case SocialType.GOOGLE -> "sign-google";
        };
    }

    static public SocialType fromValue(String value) {
        for (SocialType status : SocialType.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return switch (this) {
            case SocialType.KAKAO -> "카카오";
            case SocialType.NAVER -> "네이버";
            case SocialType.APPLE -> "애플";
            case SocialType.GOOGLE -> "구글";
            case SocialType.FACEBOOK -> "페이스북";
        };
    }
}
