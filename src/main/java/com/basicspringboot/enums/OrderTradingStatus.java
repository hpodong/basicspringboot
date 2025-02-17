package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum OrderTradingStatus implements BSEnum<OrderTradingStatus> {
    WAITING("waiting"),
    PRODUCT_WAITING("product-waiting"),
    IN_SHIPPING("in-shipping"),
    COMPLETED("completed");

    private final String value;

    OrderTradingStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<OrderTradingStatus> enumFromString(String value) {
        return fromValue(value);
    }

    static public OrderTradingStatus fromValue(String value) {
        for (OrderTradingStatus status : OrderTradingStatus.values()) {
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
}
