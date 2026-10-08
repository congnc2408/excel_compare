package com.example.cici.configs.excelconfigs;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporter {

    public void exportComparedData(File fileToSave, ExcelData file1, ExcelData file2) throws Exception {
        // 1. Tạo Workbook (file Excel) và Sheet mới
        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(fileToSave)) {

            Sheet sheet = workbook.createSheet("Compared Data");

            // 2. Tạo CellStyle cho Header (In đậm)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 3. Tạo CellStyle cho các ô bị lệch dữ liệu (Nền đỏ nhạt, chữ đỏ đậm)
            CellStyle diffStyle = workbook.createCellStyle();
            diffStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex()); // Màu nền đỏ nhạt
            diffStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font diffFont = workbook.createFont();
            diffFont.setColor(IndexedColors.RED.getIndex()); // Chữ màu đỏ
            diffFont.setBold(true);
            diffStyle.setFont(diffFont);

            // 4. Viết Header cho Sheet
            Row rowHeader = sheet.createRow(0);
            int colIndex = 0;

            // Header cho File 1
            for (String h1 : file1.headers()) {
                Cell cell = rowHeader.createCell(colIndex++);
                cell.setCellValue(h1 + " (F1)");
                cell.setCellStyle(headerStyle);
            }

            // Tạo cột phân cách
            sheet.setColumnWidth(colIndex, 1000); // Làm cột nhỏ lại
            colIndex++;

            // Header cho File 2
            for (String h2 : file2.headers()) {
                Cell cell = rowHeader.createCell(colIndex++);
                cell.setCellValue(h2 + " (F2)");
                cell.setCellStyle(headerStyle);
            }

            // 5. Viết Body (Dữ liệu) và Tô màu
            int maxRows = Math.max(file1.body().size(), file2.body().size());

            for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
                // Apache POI bắt đầu ghi body từ dòng số 1 (do dòng 0 là Header)
                Row row = sheet.createRow(rowIndex + 1);
                colIndex = 0;

                // Ghi dữ liệu File 1
                for (String header : file1.headers()) {
                    Cell cell = row.createCell(colIndex++);

                    if (rowIndex < file1.body().size()) {
                        Object val1 = file1.body().get(rowIndex).get(header);
                        String strVal1 = val1 != null ? val1.toString() : "";
                        cell.setCellValue(strVal1);

                        // KIỂM TRA SO SÁNH VÀ TÔ MÀU
                        if (ExcelComparator.hasDifferent(file1, file2, rowIndex, header)) {
                            cell.setCellStyle(diffStyle);
                        }
                    }
                }

                // Nhảy qua cột phân cách
                colIndex++;

                // Ghi dữ liệu File 2
                for (String header : file2.headers()) {
                    Cell cell = row.createCell(colIndex++);

                    if (rowIndex < file2.body().size()) {
                        Object val2 = file2.body().get(rowIndex).get(header);
                        String strVal2 = val2 != null ? val2.toString() : "";
                        cell.setCellValue(strVal2);

                        // KIỂM TRA SO SÁNH VÀ TÔ MÀU
                        // Chú ý đảo ngược vị trí file1 và file2 cho hàm hasDifference
                        if (ExcelComparator.hasDifferent(file2, file1, rowIndex, header)) {
                            cell.setCellStyle(diffStyle);
                        }
                    }
                }
            }

            // (Tùy chọn) Auto-size các cột cho đẹp
            for (int i = 0; i < file1.headers().size() + file2.headers().size() + 1; i++) {
                sheet.autoSizeColumn(i);
            }

            // 6. Lưu file xuống ổ cứng
            workbook.write(fos);
        }
    }
}
