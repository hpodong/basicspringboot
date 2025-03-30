package com.basicspringboot.filters.interfaces;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.List;

public interface SheetSetter{
    String setFileName();
    void setHeaders(List<String> headers);
    void setRows(int index, Row row, Workbook workbook);
    void setSheet(Sheet sheet);
    void onSuccess(Workbook workbook, String filename) throws IOException;
}
