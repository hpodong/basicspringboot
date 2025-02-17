package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum ProductStatus implements BSEnum<ProductStatus> {
    ACTIVE("active"),
    PAUSED("paused"),
    SOLD_OUT("sold-out");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<ProductStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public String toClass() {
        return switch (this) {
            case ACTIVE -> "txt-blue";
            case PAUSED, SOLD_OUT -> "txt-red";
        };
    }

    @Override
    public String toHtml() {
        return switch (this) {
            case ACTIVE -> "판매중";
            case PAUSED -> "판매금지";
            case SOLD_OUT -> "품절";
        };
    }

    static public ProductStatus fromValue(String value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
