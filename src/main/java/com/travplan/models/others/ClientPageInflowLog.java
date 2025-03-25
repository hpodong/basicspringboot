package com.travplan.models.others;

import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.models._BSModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@BSTable(
        name = "client_page_inflow_log",
        primaryKey = "cpifl_idx",
        createdAt = "cpifl_crdt"
)
public class ClientPageInflowLog extends _BSModel {

    @BSColumn(name = "cpifl_idx")
    private Long idx;

    @BSColumn(name = "cp_idx")
    private Long client_page_idx;

    @BSColumn(name = "cpifl_crdt")
    private Timestamp created_at;

    @BSColumn(name = "COUNT(*)", isQuerySelect = true)
    private Long count;

    private ClientPage client_page;

    public ClientPageInflowLog(Long idx) {
        this.client_page_idx = idx;
        created_at = Timestamp.valueOf(LocalDateTime.now());
    }

    public ClientPageInflowLog(ResultSet rs, int row_num) {
        super(rs, row_num);
        client_page = new ClientPage(rs, row_num);
    }

    public ClientPageInflowLog(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
        client_page = new ClientPage(rs, row_num);
    }

    public double getRate(Long total_count) {
        double rate = (double) count / total_count * 100;
        return Math.round(rate * 10) / 10.0;
    }
}
