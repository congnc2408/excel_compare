package com.example.cici.configs;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.Map;

import com.example.cici.configs.excelconfigs.ExcelComparator;
import com.example.cici.configs.excelconfigs.ExcelData;

public class ViewTableHelper {

    public void populateTableView(TableView<Map<String, Object>> tableView, ExcelData excelData) {

        tableView.getColumns().clear();
        tableView.getItems().clear();

        for (String header : excelData.headers()) {
            TableColumn<Map<String, Object>, Object> column = createColumns(header, header);
            tableView.getColumns().add(column);
        }
        tableView.setItems(FXCollections.observableArrayList(excelData.body()));
    }

    private TableColumn<Map<String, Object>, Object> createColumns(String title, String columnKey) {
        TableColumn<Map<String, Object>, Object> columns = new TableColumn<>(title);
        columns.setCellValueFactory(cellData -> {
            Map<String, Object> rowMap = cellData.getValue();
            return new SimpleObjectProperty<>(rowMap.get(columnKey));
        });
        return columns;
    }

    public void applyComparison(TableView<Map<String, Object>> table, ExcelData data1, ExcelData data2) {
        table.getColumns().clear();

        TableColumn<Map<String, Object>, Number> sttCol = new TableColumn<>("STT");
        sttCol.setPrefWidth(50);
        sttCol.setStyle("-fx-alignment: CENTER");
        sttCol.setCellFactory(column -> new TableCell<Map<String, Object>, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        table.getColumns().add(sttCol);
        for (String header : data1.headers()) {
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(header);
            col.setCellValueFactory(celldata -> new ReadOnlyObjectWrapper<>(celldata.getValue().get(header)));
            col.setCellFactory(colum -> new TableCell<Map<String, Object>, Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item != null ? item.toString() : "");
                        boolean isDifferent = ExcelComparator.hasDifferent(data1, data2, getIndex(), header);
                        if (isDifferent) {
                            setStyle("-fx-background-color: #ffebee; -fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            });
            table.getColumns().add(col);
        }
        table.setItems(FXCollections.observableArrayList(data1.body()));

    }
}
