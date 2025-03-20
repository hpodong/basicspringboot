package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum MemberGender implements BSEnum<MemberGender> {
    MAN("man"),
    WOMAN("woman");

    private final String value;

    MemberGender(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<MemberGender> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public BSEnum<MemberGender> enumFromName(String name) {
        return null;
    }

    @Override
    public String getClassName() {
        return "";
    }

    static public MemberGender fromValue(String value) {
        for (MemberGender status : MemberGender.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return switch (this) {
            case MemberGender.MAN -> "남자";
            case MemberGender.WOMAN -> "여자";
        };
    }

    static public MemberGender fromNaver(String value) {
        if(value == null) return null;
        return switch (value) {
            case "M" -> MemberGender.MAN;
            case "F" -> MemberGender.WOMAN;
            default -> null;
        };
    }

    static public MemberGender fronKakao(String value) {
        if(value == null) return null;
        return switch (value) {
            case "male" -> MemberGender.MAN;
            case "female" -> MemberGender.WOMAN;
            default -> null;
        };
    }
}
