package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum ShippingStatus implements BSEnum<ShippingStatus> {
    WAITING("waiting"),
    PRODUCT_WAITING("product-waiting"),
    IN_SHIPPING("in-shipping"),
    COMPLETED("completed");

    private final String value;

    ShippingStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<ShippingStatus> enumFromString(String value) {
        return fromValue(value);
    }

    static public ShippingStatus fromValue(String value) {
        for (ShippingStatus status : ShippingStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toHtml() {
        return switch (this) {
            case WAITING -> "배송대기";
            case PRODUCT_WAITING -> "상품준비중";
            case IN_SHIPPING -> "배송중";
            case COMPLETED -> "배송완료";
        };
    }

    @Override
    public String toClass() {
        return switch (this) {
            case WAITING -> "cr999";
            case PRODUCT_WAITING -> "brown-txt";
            case IN_SHIPPING -> "mustard-txt";
            case COMPLETED -> "blue-txt";
        };
    }

    public int getIndex() {
        return switch (this) {
            case WAITING -> 1;
            case PRODUCT_WAITING -> 2;
            case IN_SHIPPING -> 3;
            case COMPLETED -> 4;
        };
    }
}
