package com.basicspringboot.services.client;

import com.google.common.net.HttpHeaders;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.interfaces.SheetSetter;
import com.basicspringboot.models.others.ClientPageInflowLog;
import com.basicspringboot.services._BSService;
import com.basicspringboot.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@Getter
public class ClientPageInflowLogService extends _BSService<ClientPageInflowLog> {

    public boolean insert(ClientPageInflowLog data) {
        return super.insert(ClientPageInflowLog.class, setter -> setter.putAll(data.toInput()));
    }

    public long findAllTotalCount(BSQuery bsq) {
        return jt.queryForObject(bsq.toObjectSql("COUNT(*)"), Long.class);
    }

    public void downloadExcel(BSQuery bsq) throws IOException {
        bsq.setLimit(null);
        final List<ClientPageInflowLog> list = findAll(bsq, ClientPageInflowLog::new);
        final long total_count = findAllTotalCount(bsq);
        downloadExcel(list, new SheetSetter() {
            int rowIndex = 1;
            @Override
            public String setFileName() {
                return "페이지 접속 내역";
            }

            @Override
            public void setHeaders(List<String> headers) {
                headers.add("번호");
                headers.add("날짜");
                headers.add("페이지");
                headers.add("방문자 수");
                headers.add("비율");
            }

            @Override
            public void setRows(int index, Row row, Workbook workbook) {
                final ClientPageInflowLog log = list.get(index);
                final CellStyle centerStyle = getCenterStyle(workbook);
                final CellStyle leftStyle = getLeftStyle(workbook);
                final CellStyle rightStyle = getRightStyle(workbook);

                row.createCell(0).setCellValue(rowIndex++);
                row.createCell(1).setCellValue(Utils.formatTimestamp(log.getCreated_at()));
                row.createCell(2).setCellValue(log.getClient_page().getTitle());
                row.createCell(3).setCellValue(log.getCount());
                row.createCell(4).setCellValue(log.getRate(total_count)+"%");

                row.getCell(0).setCellStyle(centerStyle);
                row.getCell(1).setCellStyle(centerStyle);
                row.getCell(2).setCellStyle(leftStyle);
                row.getCell(3).setCellStyle(rightStyle);
                row.getCell(4).setCellStyle(rightStyle);
            }

            @Override
            public void setSheet(Sheet sheet) {
                sheet.setColumnWidth(0, 10 * BASIC_WIDTH);
                sheet.setColumnWidth(1, 15 * BASIC_WIDTH);
                sheet.setColumnWidth(2, 30 * BASIC_WIDTH);
                sheet.setColumnWidth(3, 20 * BASIC_WIDTH);
                sheet.setColumnWidth(4, 20 * BASIC_WIDTH);
            }

            @Override
            public void onSuccess(Workbook workbook, String filename) throws IOException {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".xlsx");
                response.setCharacterEncoding("UTF-8");

                workbook.write(response.getOutputStream());
                workbook.close();
            }
        });
    }
}
