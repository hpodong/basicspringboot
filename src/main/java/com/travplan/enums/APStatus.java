package com.travplan.enums;

import com.travplan.interfaces.BSEnum;

public enum APStatus implements BSEnum<APStatus> {
    ACTIVATED("activated"),
    PAUSED("paused");

    private final String value;

    APStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<APStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public BSEnum<APStatus> enumFromName(String name) {
        return null;
    }

    @Override
    public String getClassName() {
        return switch (this) {
            case APStatus.ACTIVATED -> "txt-blue";
            case APStatus.PAUSED -> "txt-red";
        };
    }

    static public APStatus fromValue(String value) {
        for (APStatus status : APStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return switch (this) {
            case APStatus.ACTIVATED -> "활성화";
            case APStatus.PAUSED -> "비활성화";
        };
    }
}
