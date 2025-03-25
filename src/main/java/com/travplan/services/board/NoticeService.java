package com.travplan.services.board;

import com.travplan.models.board.Notice;
import com.travplan.models.others.FileModel;
import com.travplan.services.others.FileService;
import com.travplan.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Getter
public class NoticeService extends _BSService<Notice> {

    @Autowired
    private FileService fileService;

    public boolean update(Notice data) {
        return update(data, false);
    }

    public boolean update(Notice data, boolean nullable) {
        final String where = "WHERE n_idx = "+data.getIdx();
        return super.update(Notice.class, where, setter -> setter.putAll(data.toInput(nullable)));
    }

    @Transactional
    public void insertFiles(List<FileModel> files, Long data_idx) {
        insertFiles(files, data_idx, null);
    }

    @Transactional
    public void insertFiles(List<FileModel> files, Long data_idx, Integer initSort) {
        if(files != null) {
            final int length = files.size();
            for (int index = 0; index < length; index++) {
                int sort = index+1;
                if(initSort != null) sort += initSort;
                final FileModel file = files.get(index);
                final Long file_idx = fileService.insertReturnKey(file);
                if(file_idx != null && insertFile(file_idx, data_idx, sort)) {
                    file.create();
                }
            }
        }
    }

    public Map<String, Object> getFileMaxSortFromDataIdx(Long idx) {
        final String sql = """
                SELECT MAX(nf_sort), COUNT(*) FROM notice_file
                WHERE n_idx = ?
                """;
        return master.queryForObject(sql, (rs, rn) -> {
            final Map<String, Object> data = new HashMap<>();
            data.put("max", rs.getInt("MAX(nf_sort)"));
            data.put("count", rs.getInt("COUNT(*)"));
            return data;
        }, idx);
    }

    private boolean insertFile(Long file_idx, Long data_room_idx, int sort) {
        return insert("notice_file", setter -> {
            setter.put("n_idx", data_room_idx);
            setter.put("f_idx", file_idx);
            setter.put("nf_sort", sort);
        });
    }

    public List<FileModel> getFilesFromDataIdx(Long idx) {
        final String sql = """
                SELECT f.* FROM notice_file nf
                JOIN file f ON f.f_idx = nf.f_idx
                WHERE n_idx = ?
                ORDER BY nf_sort
                """;
        return master.query(sql, (rs, rn) -> new FileModel(rs, rn), idx);
    }
}
