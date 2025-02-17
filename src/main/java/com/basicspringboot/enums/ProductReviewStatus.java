package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum ProductReviewStatus implements BSEnum<ProductReviewStatus> {
    SHOW("show"),
    HIDE("hide");

    private final String value;

    ProductReviewStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<ProductReviewStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public String toClass() {
        return switch (this) {
            case ProductReviewStatus.SHOW -> "txt-blue";
            case ProductReviewStatus.HIDE -> "txt-red";
        };
    }

    @Override
    public String toHtml() {
        return switch (this) {
            case ProductReviewStatus.SHOW -> "노출";
            case ProductReviewStatus.HIDE -> "미노출";
        };
    }

    static public ProductReviewStatus fromValue(String value) {
        for (ProductReviewStatus status : ProductReviewStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
