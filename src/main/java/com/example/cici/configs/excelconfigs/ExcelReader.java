package com.example.cici.configs.excelconfigs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {

    public ExcelData readExcelFile(File file) throws EncryptedDocumentException, IOException {
        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            List<String> headers = readHeaders(sheet);
            List<Map<String, Object>> body = readBody(sheet, headers);
            return new ExcelData(headers, body);
        }
    }

    private List<String> readHeaders(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        Row headerRow = sheet.getRow(0);

        if (headerRow != null) {
            // for (Cell cell : headerRow) {
            // headers.add(extractCellValue(cell).toString());
            // }

            int maxCols = headerRow.getLastCellNum();
            for (int i = 0; i < maxCols; i++) {
                Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                String headerName = "";
                if (cell != null) {
                    headerName = cell.getStringCellValue().trim();
                }
                if (!headerName.isEmpty()) {
                    headers.add(headerName);
                } else {
                    continue;
                }
            }
        }
        return headers;
    }

    private List<Map<String, Object>> readBody(Sheet sheet, List<String> headers) {
        List<Map<String, Object>> data = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();

        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            Map<String, Object> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                rowData.put(headers.get(j), extractCellValue(cell));
            }
            data.add(rowData);
        }
        return data;
    }

    private Object extractCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                // Kiểm tra xem số này có phải là ngày tháng (Date) không
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString(); // Hoặc format ra String tùy ý
                } else {
                    // Nếu là số thập phân nhưng phần đuôi là .0 thì cắt đi cho đẹp
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        yield String.valueOf((long) value);
                    }
                    yield String.valueOf(value);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                // Nếu là công thức, lấy kết quả tính toán của công thức đó (dạng String)
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BLANK, _NONE, ERROR -> ""; // Các trường hợp rỗng hoặc lỗi trả về chuỗi rỗng
            default -> "";
        };
    }

}
