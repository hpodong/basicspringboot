package com.travplan.interfaces;

import java.sql.ResultSet;

public interface AdminListViewSetter<T> {
    T setter(Integer offset, long count, ResultSet rs, int row_num);
}
