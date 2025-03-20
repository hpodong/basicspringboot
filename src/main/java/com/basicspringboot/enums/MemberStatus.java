package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum MemberStatus implements BSEnum<MemberStatus> {
    WAITING("waiting"),
    ACTIVE("active"),
    CANCELED("canceled"),
    LEAVED("leaved");

    private final String value;

    MemberStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<MemberStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public BSEnum<MemberStatus> enumFromName(String name) {
        return null;
    }

    @Override
    public String getClassName() {
        return switch (this) {
            case MemberStatus.WAITING -> "txt-yellow";
            case MemberStatus.ACTIVE -> "txt-blue";
            case MemberStatus.CANCELED, MemberStatus.LEAVED -> "txt-red";
        };
    }

    static public MemberStatus fromValue(String value) {
        for (MemberStatus status : MemberStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return switch (this) {
            case MemberStatus.WAITING-> "대기";
            case MemberStatus.ACTIVE -> "승인";
            case MemberStatus.CANCELED -> "거절";
            case MemberStatus.LEAVED -> "탈퇴";
        };
    }
}
