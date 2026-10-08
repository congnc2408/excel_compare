package com.example.cici;

import com.example.cici.configs.AlertUtils;
import com.example.cici.configs.ViewTableHelper;
import com.example.cici.configs.excelconfigs.ExcelData;
import com.example.cici.configs.excelconfigs.ExcelExporter;
import com.example.cici.configs.excelconfigs.ExcelReader;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;

public class MainController {
    // @FXML
    // private Label welcomeText;

    @FXML
    private Label lblRoot;

    @FXML
    private Label lblCompare;
    @FXML
    private TableView<Map<String, Object>> tblRoot;

    @FXML
    private TableView<Map<String, Object>> tblCompare;

    @FXML
    private Button btnCompare;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnExport;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label lblProgress;

    private ExcelData file1Data = null;
    private ExcelData file2Data = null;

    private ExcelReader reader = new ExcelReader();
    private ViewTableHelper viewTable = new ViewTableHelper();

    private void setupDragAndDrop(TableView<Map<String, Object>> tableView, boolean isFile1) {
        tableView.setOnDragOver(event -> {
            if (event.getGestureSource() != tableView && event.getDragboard().hasFiles()) {
                File droppedFile = event.getDragboard().getFiles().get(0);
                String name = droppedFile.getName().toLowerCase();

                event.acceptTransferModes(TransferMode.COPY);

            }
            event.consume();
        });

        tableView.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles()) {
                tableView.setStyle("-fx-border-color: #2196F3 ; -fx-border-width: 2px;");
            }
            event.consume();
        });
        tableView.setOnDragExited(event -> {
            tableView.setStyle("");
            event.consume();
        });
        tableView.setOnDragDropped(event -> {
            boolean success = true;
            var db = event.getDragboard();

            if (db.hasFiles()) {
                File droppedFile = db.getFiles().get(0);
                String name = droppedFile.getName().toLowerCase();
                ExcelData data;
                try {
                    data = reader.readExcelFile(droppedFile);
                    if (isFile1) {
                        file1Data = data;
                        lblRoot.setText(name);
                        handleFileSelected(tableView, droppedFile, data);
                    } else {
                        file2Data = data;
                        lblCompare.setText(name);
                        handleFileSelected(tableView, droppedFile, data);
                    }
                } catch (EncryptedDocumentException | IOException e) {
                    e.printStackTrace();
                }

                success = true;
            }

            event.setDropCompleted(success);
            tableView.setStyle("");
            event.consume();
        });
    }

    @FXML
    public void initialize() {
        progressBar.setVisible(false);
        lblProgress.setVisible(false);

        setupDragAndDrop(tblRoot, true);
        setupDragAndDrop(tblCompare, false);

        btnCompare.setOnAction(e -> handleCompareAction());
        btnReset.setOnAction(e -> Reset(e));
        // btnClose.setOnAction(e -> Close(e));
    }

    @FXML
    private void handleExportAction() {
        if (file1Data == null || file2Data == null) {
            System.out.println("Cần load đủ 2 file và Compare trước khi Export!");
            return;
        }

        // 1. Mở cửa sổ cho người dùng chọn nơi lưu file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu kết quả so sánh");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Ket_qua_so_sanh.xlsx");

        File fileToSave = fileChooser.showSaveDialog(tblRoot.getScene().getWindow());

        if (fileToSave != null) {
            // 2. Gọi hàm ghi file Excel (Ta sẽ viết hàm này ở bước sau)
            ExcelExporter exporter = new ExcelExporter();
            try {
                exporter.exportComparedData(fileToSave, file1Data, file2Data);
                System.out.println("Xuất file thành công!");

                // Bạn có thể show một Alert thông báo thành công ở đây
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lỗi khi xuất file!");
            }
        }
    }

    private void handleFileSelected(TableView<Map<String, Object>> tbl, File file, ExcelData data) {
        try {
            ExcelReader reader = new ExcelReader();
            ViewTableHelper uiHelper = new ViewTableHelper();

            // ExcelData data = reader.readExcelFile(file);

            uiHelper.populateTableView(tbl, data);

        } catch (Exception e) {
            AlertUtils.showErrorAlert("Error", "Error reading Excel file", e.getMessage());
        }
    }

    /**
     * 
     */
    private void handleCompareAction() {
        if (file1Data == null || file2Data == null) {
            AlertUtils.showWarningAlert("Warning", "Please select 2 files to compare", null);
            return;
        }
        progressBar.setVisible(true);
        lblProgress.setVisible(true);
        Task<Void> compareTask = new Task<>() {

            @Override
            protected Void call() throws Exception {
                int totalRow = Math.min(file1Data.body().size(), file2Data.body().size());
                for (int i = 0; i < totalRow; i++) {
                    Thread.sleep(1);
                    updateProgress(i + 1, totalRow);
                    double percent = ((double) (i + 1) / totalRow) * 100;
                    updateMessage(String.format("%.0f%%", percent));
                }
                return null;
            }

        };

        progressBar.progressProperty().bind(compareTask.progressProperty());
        lblProgress.textProperty().bind(compareTask.messageProperty());
        compareTask.setOnSucceeded(event -> {
            progressBar.progressProperty().unbind();
            lblProgress.textProperty().unbind();
            progressBar.setVisible(false);
            lblProgress.setVisible(false);
            viewTable.applyComparison(tblRoot, file1Data, file2Data);
            viewTable.applyComparison(tblCompare, file2Data, file1Data);

        });

        compareTask.setOnFailed(event -> {
            progressBar.progressProperty().unbind();
            lblProgress.textProperty().unbind();
            AlertUtils.showErrorAlert("Error", "Error comparing files", compareTask.getException().getMessage());
        });

        new Thread(compareTask).start();
    }

    public void Reset(ActionEvent actionEvent) {
        tblRoot.getItems().clear();
        tblRoot.getColumns().clear();
        tblCompare.getItems().clear();
        tblCompare.getColumns().clear();
    }

}
