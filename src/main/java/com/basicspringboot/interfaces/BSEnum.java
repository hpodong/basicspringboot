package com.basicspringboot.interfaces;

public interface BSEnum<T extends Enum<T>> {
    String getValue();

    BSEnum<T> enumFromString(String value);

    String toClass();

    String toHtml();
}