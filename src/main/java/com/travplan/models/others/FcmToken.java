package com.travplan.models.others;

import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@BSTable(
        name = "fcm_token",
        primaryKey = "ft_idx",
        createdAt = "ft_crdt",
        updatedAt = "ft_updt"
)
public class FcmToken extends _BSModel {

    @BSColumn(name = "ft_idx")
    private Long idx;

    @BSColumn(name = "m_idx")
    private Long member_idx;

    @BSColumn(name = "ft_token")
    private String token;

    @BSColumn(name = "ft_device_id")
    private String device_id;

    @BSColumn(name = "ft_crdt")
    private Timestamp created_at;

    @BSColumn(name = "ft_updt")
    private Timestamp updated_at;

    public FcmToken(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public FcmToken(HttpServletRequest request) {
        super(request);
    }

    public FcmToken(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
}
