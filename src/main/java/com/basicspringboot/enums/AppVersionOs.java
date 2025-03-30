package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum AppVersionOs implements BSEnum<AppVersionOs> {
    ANDROID("A"),
    IOS("I");

    private final String value;

    AppVersionOs(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<AppVersionOs> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public BSEnum<AppVersionOs> enumFromName(String name) {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    static public AppVersionOs fromValue(String value) {
        for (AppVersionOs status : AppVersionOs.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return switch (this) {
            case AppVersionOs.ANDROID -> "AOS";
            case AppVersionOs.IOS -> "iOS";
        };
    }
}
