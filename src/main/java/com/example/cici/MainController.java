package com.example.cici;

import com.example.cici.configs.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class MainController {
//    @FXML
//    private Label welcomeText;

    @FXML
    private TableView<Map<String,Object>> tblRoot;

    @FXML
    private TableView<Map<String,Object>> tblCompare;

    @FXML
    private Button btnCompare;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnClose;


    @FXML
    public void initialize(){
        tblRoot.setOnDragOver(event -> {
            if (event.getGestureSource() != tblRoot && event.getDragboard().hasFiles()){
                File droppedFile = event.getDragboard().getFiles().get(0);
                String name = droppedFile.getName().toLowerCase();

                event.acceptTransferModes(TransferMode.COPY);

            }
            event.consume();
        });

        tblRoot.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles()){
                tblRoot.setStyle("-fx-border-color: #2196F3 ; -fx-border-width: 2px;");

            }
            event.consume();
        });
        tblRoot.setOnDragExited(event -> {
            tblRoot.setStyle("");
            event.consume();
        });
        tblRoot.setOnDragDropped(event -> {
            boolean success = true;
            var db = event.getDragboard();

            if (db.hasFiles()){
                File droppedFile = db.getFiles().get(0);
                String name = droppedFile.getName().toLowerCase();
                AlertUtils.showInformationAlert("Notification","file đã drop" +name,"");

                success = true;
            }

            event.setDropCompleted(success);
            tblRoot.setStyle("");
            event.consume();
        });
    }



    public void Reset(ActionEvent actionEvent){
        tblRoot.getItems().clear();
        tblCompare.getItems().clear();
    }


}
