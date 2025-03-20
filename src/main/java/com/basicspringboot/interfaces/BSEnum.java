package com.basicspringboot.interfaces;

public interface BSEnum<T extends Enum<T>> {
    String getValue();

    BSEnum<T> enumFromString(String value);

    BSEnum<T> enumFromName(String name);

    String getClassName();

    String getName();
}