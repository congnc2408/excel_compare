package com.example.cici.configs;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtils {

    public static void showInformationAlert(String title, String header, String content) {
        createAndShowMessage(AlertType.INFORMATION, title, header, content);
    }

    public static void showWarningAlert(String title, String header, String content) {
        createAndShowMessage(AlertType.WARNING, title, header, content);
    }

    public static void showErrorAlert(String title, String header, String content) {
        createAndShowMessage(AlertType.ERROR, title, header, content);
    }

    private  static void createAndShowMessage(Alert.AlertType type, String title, String header, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
