package com.example.cici.configs;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.Map;

public class ViewTable {
    public static <T> TableColumn<Map<String,Object>,T> createColumns(String title, String columnKey){
        TableColumn<Map<String,Object>,T> columns = new TableColumn<>(title);
        columns.setCellValueFactory(cellData ->{
           Map<String, Object> rowMap = cellData.getValue();
           @SuppressWarnings("unchecked")
           T value = (T) rowMap.get(columnKey);
            return new SimpleObjectProperty<>(value);
        });
        return columns;
    }
}
