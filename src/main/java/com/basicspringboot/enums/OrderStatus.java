package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum OrderStatus implements BSEnum<OrderStatus> {
    CANCELED("canceled"),
    WAITING("waiting"),
    PURCHASED("purchased"),
    PRODUCT_WAITING("product-waiting"),
    IN_SHIPPING("in-shipping"),
    SHIPPING_COMPLETED("shipping-completed"),
    ALL_COMPLETED("all-completed");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<OrderStatus> enumFromString(String value) {
        return fromValue(value);
    }

    @Override
    public String toClass() {
        return switch (this) {
            case OrderStatus.CANCELED -> "red-txt";
            case OrderStatus.WAITING -> "lightsky-txt";
            case OrderStatus.PURCHASED -> "green-txt";
            case OrderStatus.PRODUCT_WAITING -> "brown-txt";
            case OrderStatus.IN_SHIPPING -> "mustard-txt";
            case OrderStatus.SHIPPING_COMPLETED -> "blue-txt";
            case OrderStatus.ALL_COMPLETED -> "gray-txt";
        };
    }

    public String toContainerClass() {
        return switch (this) {
            case OrderStatus.CANCELED -> "order_reception";
            case OrderStatus.WAITING -> "order_reception";
            case OrderStatus.PURCHASED -> "pay_completed";
            case OrderStatus.PRODUCT_WAITING -> "preparing_product";
            case OrderStatus.IN_SHIPPING -> "in_delivery";
            case OrderStatus.SHIPPING_COMPLETED -> "delivery_completed";
            case OrderStatus.ALL_COMPLETED -> "purchase_confirm";
        };
    }

    public String toEmptyText() {
        return switch (this) {
            case OrderStatus.CANCELED -> "주문취소된 상품이 없습니다.";
            case OrderStatus.WAITING -> "주문접수된 상품이 없습니다.";
            case OrderStatus.PURCHASED -> "결제완료된 상품이 없습니다.";
            case OrderStatus.PRODUCT_WAITING -> "준비중인 상품이 없습니다.";
            case OrderStatus.IN_SHIPPING -> "배송중인 상품이 없습니다.";
            case OrderStatus.SHIPPING_COMPLETED -> "배송완료 된 상품이 없습니다.";
            case OrderStatus.ALL_COMPLETED -> "구매확정된 상품이 없습니다.";
        };
    }

    static public OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toHtml() {
        return switch (this) {
            case OrderStatus.CANCELED -> "주문취소";
            case OrderStatus.WAITING -> "주문접수";
            case OrderStatus.PURCHASED -> "결제완료";
            case OrderStatus.PRODUCT_WAITING -> "상품준비중";
            case OrderStatus.IN_SHIPPING -> "배송중";
            case OrderStatus.SHIPPING_COMPLETED -> "배송완료";
            case OrderStatus.ALL_COMPLETED -> "구매확정";
        };
    }

    public int getIndex() {
        return switch (this) {
            case OrderStatus.CANCELED -> 0;
            case OrderStatus.WAITING -> 1;
            case OrderStatus.PURCHASED -> 2;
            case OrderStatus.PRODUCT_WAITING -> 3;
            case OrderStatus.IN_SHIPPING -> 4;
            case OrderStatus.SHIPPING_COMPLETED -> 5;
            case OrderStatus.ALL_COMPLETED -> 6;
        };
    }

    public boolean isWaiting() {
        return this.getIndex() <= 2;
    }
}
