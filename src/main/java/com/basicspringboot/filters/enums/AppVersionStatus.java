package com.basicspringboot.filters.enums;

import com.basicspringboot.filters.interfaces.BSEnum;

public enum AppVersionStatus implements BSEnum<AppVersionStatus> {
    PASS("pass"),
    UPDATE("update"),
    PUSH("push"),
    INSPECTION("inspection");

    private final String value;

    AppVersionStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<AppVersionStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public BSEnum<AppVersionStatus> enumFromName(String name) {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    static public AppVersionStatus fromValue(String value) {
        for (AppVersionStatus status : AppVersionStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return switch (this) {
            case AppVersionStatus.PASS -> "pass";
            case AppVersionStatus.UPDATE -> "update";
            case AppVersionStatus.PUSH -> "push";
            case AppVersionStatus.INSPECTION -> "inspection";
        };
    }
}
