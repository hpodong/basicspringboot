package com.basicspringboot.models.logs;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.models._BSModel;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Setter
@ToString
@BSTable(name = "traffic", primaryKey = "t_idx", createdAt = "t_crdt")
public class Traffic extends _BSModel {

    @BSColumn(name = "t_idx")
    private Long idx;
    @BSColumn(name = "t_value")
    private Long value;
    @BSColumn(name = "t_crdt")
    private Date createdAt;
}
