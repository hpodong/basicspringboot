package com.basicspringboot.interfaces;

import java.util.List;

public interface InsertSetter {
    void columnSetter(List<String> columns);
    void valueSetter(int index, List<Object> values);
}
