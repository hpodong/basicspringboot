package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum MemberPointStatus implements BSEnum<MemberPointStatus> {
    SAVED("saved"), //적립
    USED("used"), //사용
    EXPIRED("expired"), //만료
    MINUS("minus"); //차감

    private final String value;

    MemberPointStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<MemberPointStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public String toClass() {
        return switch (this) {
            case MemberPointStatus.SAVED -> "lightsky-txt";
            case MemberPointStatus.USED -> "green-txt";
            case MemberPointStatus.EXPIRED -> "cr999";
            case MemberPointStatus.MINUS -> "txt-red";
        };
    }

    static public MemberPointStatus fromValue(String value) {
        for (MemberPointStatus status : MemberPointStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toHtml() {
        return switch (this) {
            case MemberPointStatus.SAVED -> "적립";
            case MemberPointStatus.USED -> "사용";
            case MemberPointStatus.EXPIRED -> "만료";
            case MemberPointStatus.MINUS -> "차감";
        };
    }

    public int getIndex() {
        return switch (this) {
            case MemberPointStatus.SAVED -> 0;
            case MemberPointStatus.USED -> 1;
            case MemberPointStatus.EXPIRED -> 2;
            case MemberPointStatus.MINUS -> 3;
        };
    }

    public boolean isMinus() {
        return !this.equals(SAVED);
    }
}
