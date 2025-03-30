package com.basicspringboot.filters.enums;

import com.basicspringboot.filters.interfaces.BSEnum;

public enum MemberStatus implements BSEnum<MemberStatus> {
    ACTIVE("active"),
    BANNED("banned"),
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
            case MemberStatus.ACTIVE -> "txt-blue";
            case MemberStatus.BANNED, MemberStatus.LEAVED -> "txt-red";
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
            case MemberStatus.ACTIVE -> "승인";
            case MemberStatus.BANNED -> "정지";
            case MemberStatus.LEAVED -> "탈퇴";
        };
    }
}
