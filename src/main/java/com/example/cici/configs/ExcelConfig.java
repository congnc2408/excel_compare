package com.example.cici.configs;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.cici.configs.ViewTable.createColumns;

public class ExcelConfig {
    public  void getListTitleExcel(File file, TableView<Map<String,Object>> tableView){
        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = WorkbookFactory.create(fis)){
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();

            if (!rowIterator.hasNext()) return;

            //Đọc dòng đầu tiên
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();

            for (Cell cell : headerRow){
                String headerTitle = cell.getStringCellValue();
                headers.add(headerTitle);

                TableColumn<Map<String,Object>,Object> column = createColumns(headerTitle,headerTitle);
                tableView.getColumns().add(column);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
