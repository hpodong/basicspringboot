package com.travplan.enums;

import com.travplan.interfaces.BSEnum;

public enum AdminStatus implements BSEnum<AdminStatus> {
    WAITING("waiting"),
    ACTIVE("active"),
    PAUSE("pause");

    private final String value;

    AdminStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<AdminStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public BSEnum<AdminStatus> enumFromName(String name) {
        return null;
    }

    @Override
    public String getClassName() {
        return switch (this) {
            case AdminStatus.WAITING -> "txt-yellow";
            case AdminStatus.ACTIVE -> "txt-blue";
            case AdminStatus.PAUSE -> "txt-red";
        };
    }

    static public AdminStatus fromValue(String value) {
        for (AdminStatus status : AdminStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return switch (this) {
            case AdminStatus.WAITING-> "대기";
            case AdminStatus.ACTIVE -> "승인";
            case AdminStatus.PAUSE -> "정지";
        };
    }
}
