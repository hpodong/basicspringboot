package com.basicspringboot.enums;

import com.basicspringboot.interfaces.BSEnum;

public enum OrderCancelStatus implements BSEnum<OrderCancelStatus> {
    CANCELED("canceled"),
    REQUEST_RETURN("return-request"),
    REQUEST_TRADING("trading-request"),
    CANCELED_RETURN("return-canceled"),
    CANCELED_TRADING("trading-canceled"),
    ACCEPTED_RETURN("return-accepted"),
    ACCEPTED_TRADING("trading-accepted");

    private final String value;

    OrderCancelStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BSEnum<OrderCancelStatus> enumFromString(String value) {
        return fromValue(value);
    }

    static public OrderCancelStatus fromValue(String value) {
        for (OrderCancelStatus status : OrderCancelStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toHtml() {
        return switch (this) {
            case CANCELED -> "주문취소";
            case REQUEST_RETURN -> "환불신청";
            case ACCEPTED_RETURN -> "환불승인";
            case CANCELED_RETURN -> "환불취소";
            case REQUEST_TRADING -> "교환신청";
            case ACCEPTED_TRADING -> "교환승인";
            case CANCELED_TRADING -> "교환취소";
        };
    }

    @Override
    public String toClass() {
        return switch (this) {
            case CANCELED, CANCELED_RETURN, CANCELED_TRADING -> "cr999";
            case REQUEST_RETURN -> "lightsky-txt";
            case REQUEST_TRADING -> "mustard-txt";
            case ACCEPTED_RETURN, ACCEPTED_TRADING -> "green-txt";
        };
    }

    public String toContainerClass() {
        return switch (this) {
            case CANCELED -> "order_cancel";
            case REQUEST_RETURN -> "refund_apply";
            case ACCEPTED_RETURN -> "refund_approve";
            case CANCELED_RETURN -> "refund_cancel";
            case REQUEST_TRADING -> "exchange_apply";
            case ACCEPTED_TRADING -> "exchange_approve";
            case CANCELED_TRADING -> "exchange_cancel";
        };
    }

    public boolean isCancel() {
        return this.equals(CANCELED);
    }

    public boolean isTrading() {
        return this.equals(ACCEPTED_TRADING) || this.equals(CANCELED_TRADING) || this.equals(REQUEST_TRADING);
    }

    public boolean isReturn() {
        return this.equals(ACCEPTED_RETURN) || this.equals(CANCELED_RETURN) || this.equals(REQUEST_RETURN);
    }

    public boolean isRefreshStock() {
        return this.equals(ACCEPTED_TRADING) || this.equals(ACCEPTED_RETURN) || this.equals(CANCELED);
    }

    public String classToString() {
        return switch (this) {
            case CANCELED -> "주문취소";
            case REQUEST_RETURN, CANCELED_RETURN, ACCEPTED_RETURN -> "환불";
            case REQUEST_TRADING, ACCEPTED_TRADING, CANCELED_TRADING -> "교환";
        };
    }

    public boolean isRemove() {
        return this.equals(CANCELED) || this.equals(CANCELED_RETURN) || this.equals(CANCELED_TRADING);
    }

    public boolean isReturnPoint() {
        return this.equals(ACCEPTED_RETURN) || this.equals(CANCELED);
    }
}
