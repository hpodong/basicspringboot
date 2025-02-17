package com.basicspringboot.models.logs;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.models._BSModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.Timestamp;

@BSTable(name = "admin_connect_log", primaryKey = "acl_idx", createdAt = "acl_crdt")
@Getter
@Setter
@NoArgsConstructor
public class AdminConnectLog extends _BSModel {

    @BSColumn(name = "acl_idx")
    private Long idx;

    @BSColumn(name = "a_idx")
    private Long admin_idx;

    @BSColumn(name = "ar_name", isInput = false)
    private String role;

    @BSColumn(name = "acl_remote_ip")
    private String remote_ip;

    @BSColumn(name = "acl_crdt")
    private Timestamp created_at;

    @BSColumn(name = "a_id", isInput = false)
    private String id;

    @BSColumn(name = "a_name", isInput = false)
    private String name;

    public AdminConnectLog(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public AdminConnectLog(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
}
