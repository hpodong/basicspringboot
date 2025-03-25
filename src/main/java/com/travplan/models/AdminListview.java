package com.travplan.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
public class AdminListview<T> {
    private List<T> list = new ArrayList<>();
    private long count = 0;
    private Integer pageCount = 0;
    private int firstPage = 0;
    private int lastPage = 1;
    private int curPage = 1;

    public AdminListview(List<T> list, long count, Integer limit, int curPage) {
        final int pageGroupCount = 10;
        this.list = list;
        this.count = count;
        this.curPage = curPage;
        if(limit != null) {
            this.pageCount = (int) Math.ceil((double) count / limit);
            final int pageGroup = (int) Math.ceil((double)curPage/pageGroupCount);

            this.firstPage = ((pageGroup-1) * limit) + 1;
            this.lastPage = Math.min(pageGroup * limit, pageCount);
        }
    }
}
